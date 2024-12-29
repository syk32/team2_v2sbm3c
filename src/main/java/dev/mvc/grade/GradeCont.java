package dev.mvc.grade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.mvc.tool.Tool;
import dev.mvc.tool.Upload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/grade")
public class GradeCont {
  
  @Autowired
  @Qualifier("dev.mvc.grade.GradeProc")
  private GradeProcInter gradeProc;
  
  /** 페이지당 출력할 레코드 갯수, nowPage는 1부터 시작 */
  public int record_per_page = 10;

  /** 블럭당 페이지 수, 하나의 블럭은 10개의 페이지로 구성됨 */
  public int page_per_block = 10;

  /** 페이징 목록 주소 */
  private String list_file_name = "/grade/list_by_gradeno";
  
  public GradeCont() {
    System.out.println("-> GradeCont created.");
  }
  
  /**
   * POST 요청시 새로고침 방지, POST 요청 처리 완료 → redirect → url → GET → forward -> html 데이터
   * 전송
   * @return
   */
  @GetMapping(value = "/post2get")
  public String post2get(Model model, 
      @RequestParam(name="url", defaultValue="") String url) {

    return url; // forward, /templates/...
  }
  
  /**
   * 등급 등록 폼
   * @param model
   * @param gradeVO
   * @return
   */
  @GetMapping(value = "/create")
  public String create(Model model,
      @ModelAttribute("gradeVO") GradeVO gradeVO) {
    model.addAttribute("GradeVO", gradeVO); // 수정된 GradeVO 전달
    
    return "/grade/create"; // /templates/grade/create.html
  }
  
  /**
   * 등급 등록 처리
   * @param request
   * @param session
   * @param model
   * @param gradeVO
   * @param ra
   * @return
   */
  @PostMapping(value = "/create")
  public String create(HttpServletRequest request,
                       HttpSession session,
                       Model model,
                       @ModelAttribute("gradeVO") GradeVO gradeVO,
                       RedirectAttributes ra) {
    String file1 = ""; 
    String file1saved = ""; 
    String thumb1 = ""; 
    long size1 = 0;

    String upDir = Grade.getUploadDir(); 

    MultipartFile mf = gradeVO.getFile1MF(); 
    if (mf != null && !mf.isEmpty()) { 
        file1 = mf.getOriginalFilename();
        size1 = mf.getSize();

        if (Tool.checkUploadFile(file1)) { 
            file1saved = Upload.saveFileSpring(mf, upDir); 
            if (Tool.isImage(file1saved)) {
                thumb1 = Tool.preview(upDir, file1saved, 200, 150); 
            }
        } else {
            ra.addFlashAttribute("code", "check_upload_file_fail");
            return "redirect:/grade/msg"; 
        }
    }

    gradeVO.setFile1(file1);
    gradeVO.setFile1saved(file1saved);
    gradeVO.setThumb1(thumb1);
    gradeVO.setSize1(size1);

    int cnt = this.gradeProc.create(gradeVO);
    if (cnt == 1) {
        ra.addAttribute("gradeno", gradeVO.getGradeno()); 
        ra.addAttribute("now_page", 1); 
        return "redirect:/grade/list_by_gradeno_search_paging"; 
    } else {
        ra.addFlashAttribute("code", "create_fail");
        return "redirect:/grade/msg"; 
    }
}

  
  /**
   * 등급 전체 목록(관리자)
   * @param session
   * @param model
   * @return
   */
  @GetMapping(value = "/list_all")
  public String list_all(HttpSession session, Model model) {
    
    ArrayList<GradeVO> list = this.gradeProc.list_all(); // 등급 모든 목록
    
    model.addAttribute("list", list);
    return "/grade/list_all";
  }
  
  /**
   * 유형 3
   * 등급별 목록 + 검색 + 페이징 http://localhost:9091/grade/list_by_gradeno?gradeno=5
   * http://localhost:9091/grade/list_by_gradeno?gradeno=6
   * 
   * @return
   */
  @GetMapping(value = "/list_by_gradeno_search_paging")
  public String list_by_gradeno_search_paging(
      HttpSession session,
      Model model,
      @ModelAttribute("gradeVO") GradeVO gradeVO,
      @RequestParam(name = "gradeno", defaultValue = "0") int gradeno,
      @RequestParam(name = "grade_name", defaultValue = "") String grade_name,
      @RequestParam(name = "now_page", defaultValue = "1") int now_page) {
    
    int record_per_page = 10;
    int startRow = (now_page - 1) * record_per_page + 1;
    int endRow = now_page * record_per_page;
    
    grade_name = Tool.checkNull(grade_name).trim();
    model.addAttribute("gradeno", gradeno);
    model.addAttribute("grade_name", grade_name);
    model.addAttribute("now_page", now_page);
    
    HashMap<String, Object> map = new HashMap<>();
    map.put("grade_name", grade_name);
    map.put("now_page", now_page);
    map.put("startRow", startRow);
    map.put("endRow", endRow);
    
    ArrayList<GradeVO> list = this.gradeProc.list_by_gradeno_search_paging(map);
    if (list == null || list.isEmpty()) {
      model.addAttribute("message", "등급이 없습니다.");
    } else {
      model.addAttribute("list", list);
    }
    
    int search_count = this.gradeProc.count_by_gradeno_search(map);
    String paging = this.gradeProc.pagingBox(now_page, grade_name, "/grade/list_by_gradeno_search_paging", search_count, Grade.RECORD_PER_PAGE, Grade.PAGE_PER_BLOCK);
    model.addAttribute("paging", paging);
    model.addAttribute("grade_name", grade_name);
    model.addAttribute("now_page", now_page);
    model.addAttribute("search_count", search_count);
    
    int no = search_count - ((now_page - 1) * Grade.RECORD_PER_PAGE);
    model.addAttribute("no", no);
    
    // /templates/grade/list_by_gradeno_search_paging.html
    return "/grade/list_by_gradeno_search_paging"; 
  }
  
  /**
   * 등급 조회
   * @return
   */
  @GetMapping(value = "/read")
  public String read(Model model,
      @RequestParam(name = "gradeno", defaultValue = "0") int gradeno,
      @RequestParam(name = "word", defaultValue = "") String word,
      @RequestParam(name = "now_page", defaultValue = "1") int now_page) {
    
    GradeVO gradeVO = this.gradeProc.read(gradeno);
    model.addAttribute("gradeVO", gradeVO);
    
    long size1 = gradeVO.getSize1();
    String size1_label = Tool.unit(size1);
    gradeVO.setSize1_label(size1_label);
    
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);

    return "/grade/read";
  }
  
  /**
   * 등급 수정 폼
   * @return
   */
  @GetMapping(value = "/update_text")
  public String update_text(HttpSession session,
      Model model,
      @RequestParam(name = "gradeno", defaultValue = "0") int gradeno,
      @RequestParam(name = "word", defaultValue = "") String word,
      @RequestParam(name = "now_page", defaultValue = "1") int now_page,
      RedirectAttributes ra) {
    
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    
    GradeVO gradeVO = this.gradeProc.read(gradeno);
    model.addAttribute("gradeVO", gradeVO);
    
    // /templates/grade/update_text.html
    return "/grade/update_text";
  }
  
  /**
   * 등급 수정 처리
   * @return
   */
  @PostMapping(value = "/update_text")
  public String update_text(
      HttpSession session,
      Model model,
      @ModelAttribute("gradeVO") GradeVO gradeVO,
      RedirectAttributes ra,
      @RequestParam(name = "search_word", defaultValue = "") String search_word,
      @RequestParam(name = "now_page", defaultValue = "0") int now_page) {
    
    // Redirect 시 검색어 및 현재 페이지를 유지하기 위한 파라미터 추가
    ra.addAttribute("word", search_word);
    ra.addAttribute("now_page", now_page);
    
    // evo_criteria 값 검증
    if (gradeVO.getEvo_criteria() == null || gradeVO.getEvo_criteria().trim().isEmpty()) {
      ra.addFlashAttribute("message", "진화 기준은 필수 입력 사항입니다.");
      ra.addFlashAttribute("code", "update_fail");
      return "redirect:/grade/msg"; // 실패 시 msg 페이지로 이동
    }
    
    // 등급 글 수정 처리
    try {
      int cnt = this.gradeProc.update_text(gradeVO); // 등급 글 수정
      if (cnt > 0) { // 수정 성공
        ra.addAttribute("gradeno", gradeVO.getGradeno());
        return "redirect:/grade/read"; // 성공 시 게시글 조회 페이지로 이동
      } else { // 수정 실패
        ra.addFlashAttribute("message", "등급 글 수정에 실패했습니다.");
        ra.addFlashAttribute("code", "update_fail");
        return "redirect:/grade/msg"; // 실패 시 msg 페이지로 이동
      }
    } catch (Exception e) {
      e.printStackTrace();
      ra.addFlashAttribute("message", "등급 글 수정 중 오류가 발생했습니다.");
      ra.addFlashAttribute("code", "update_fail");
      return "redirect:/grade/msg"; // 오류 발생 시 msg 페이지로 이동
    }
  }
  
  /**
   * 파일 수정 폼 http://localhost:9091/grade/update_file?gradeno=1
   * 
   * @return
   */
  @GetMapping(value = "/update_file")
  public String update_file(HttpSession session, Model model, 
         @RequestParam(name="gradeno", defaultValue="0") int gradeno,
         @RequestParam(name="word", defaultValue="") String word, 
         @RequestParam(name="now_page", defaultValue="1") int now_page) {
    
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    
    GradeVO gradeVO = this.gradeProc.read(gradeno);
    model.addAttribute("gradeVO", gradeno);

    return "/grade/update_file";

  }
  
  /**
   * 등급 삭제 폼
   * @param session
   * @param model
   * @param ra
   * @param gradeno
   * @param word
   * @param now_page
   * @return
   */
  @GetMapping(value = "/delete")
  public String delete(HttpSession session, Model model,
         RedirectAttributes ra,
         @RequestParam(name = "gradeno", defaultValue = "0") int gradeno,
         @RequestParam(name = "word", defaultValue = "") String word,
         @RequestParam(name = "now_page", defaultValue = "1") int now_page) {
    
    model.addAttribute("gradeno", gradeno);
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    
    GradeVO gradeVO = this.gradeProc.read(gradeno);
    model.addAttribute("gradeVO", gradeVO);
    
    return "/grade/delete"; // forward
  }
  
  /**
   * 등급 삭제 처리
   * @param ra
   * @param gradeno
   * @param word
   * @param now_page
   * @return
   */
  @PostMapping(value = "/delete")
  public String delete(RedirectAttributes ra,
      @RequestParam(name = "gradeno", defaultValue = "0") int gradeno,
      @RequestParam(name = "word", defaultValue = "") String word,
      @RequestParam(name = "now_page", defaultValue = "1") int now_page) {
    // -------------------------------------------------------------------
    // 파일 삭제 시작
    // -------------------------------------------------------------------
    // 삭제할 파일 정보를 읽어옴.
    GradeVO gradeVO_read = gradeProc.read(gradeno);
        
    String file1saved = gradeVO_read.getFile1saved();
    String thumb1 = gradeVO_read.getThumb1();
    
    String uploadDir = Grade.getUploadDir();
    Tool.deleteFile(uploadDir, file1saved);  // 실제 저장된 파일삭제
    Tool.deleteFile(uploadDir, thumb1);     // preview 이미지 삭제
    // -------------------------------------------------------------------
    // 파일 삭제 종료
    // -------------------------------------------------------------------
    
    this.gradeProc.delete(gradeno); // DBMS 삭제
    
 // -------------------------------------------------------------------------------------
    // 마지막 페이지의 마지막 레코드 삭제시의 페이지 번호 -1 처리
    // -------------------------------------------------------------------------------------    
    // 마지막 페이지의 마지막 10번째 레코드를 삭제후
    // 하나의 페이지가 3개의 레코드로 구성되는 경우 현재 9개의 레코드가 남아 있으면
    // 페이지수를 4 -> 3으로 감소 시켜야함, 마지막 페이지의 마지막 레코드 삭제시 나머지는 0 발생
    
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("word", word);
    
    ra.addAttribute("word", word);
    ra.addAttribute("now_page", now_page);
    
    return "redirect:/grade/list_by_gradeno_search_paging";
  }
}
