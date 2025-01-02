package dev.mvc.member;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.mvc.diary.DiaryProcInter;
import dev.mvc.dto.PageDTO;
import dev.mvc.dto.SearchDTO;
import dev.mvc.grade.Grade;
import dev.mvc.grade.GradeProcInter;
import dev.mvc.grade.GradeVO;
//import dev.mvc.loginlog.LoginlogProcInter;
import dev.mvc.member.MemberVO.UpdateValidationGroup;
import dev.mvc.tool.Tool;
import dev.mvc.tool.Upload;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberCont {
  @Autowired
  @Qualifier("dev.mvc.member.MemberProc")  // @Service("dev.mvc.member.MemberProc")
  private MemberProcInter memberProc;
  
//  @Autowired
//  @Qualifier("dev.mvc.loginlog.LoginlogProc")
//  private LoginlogProcInter loginlogProc;
  
  @Autowired
  @Qualifier("dev.mvc.grade.GradeProc")
  private GradeProcInter gradeProc;
  
//  /** 페이지당 출력할 레코드 갯수, nowPage는 1부터 시작 */
//  public int record_per_page = 10;
//
//  /** 블럭당 페이지 수, 하나의 블럭은 10개의 페이지로 구성됨 */
//  public int page_per_block = 10;

  public MemberCont() {
    System.out.println("-> MemberCont created.");
  }
  
  /**
   * 아이디 중복 확인
   * @param id
   * @return
   */
  @GetMapping(value="/checkID") // http://localhost:9091/member/checkID?id=admin
  @ResponseBody
  public String checkID(@RequestParam(name="id", defaultValue = "") String id) {    
    System.out.println("-> id: " + id);
    int cnt = this.memberProc.checkID(id);
   
    JSONObject obj = new JSONObject();
    obj.put("cnt", cnt);
    
    return obj.toString();
  }
  
  /**
   * 이메일 중복 확인
   * @param nickname
   * @return
   */
  @GetMapping(value = "/checkEMAIL")
  @ResponseBody
  public String checkEMAIL(@RequestParam(name = "email", defaultValue = "") String email) {
    System.out.println("-> email: " + email);
    int cnt = this.memberProc.checkEMAIL(email);
    System.out.println(cnt);
    
    JSONObject obj = new JSONObject();
    obj.put("cnt", cnt);
    
    return obj.toString();
  }

  /**
   * 회원 가입 폼
   * @param model
   * @param memberVO
   * @return
   */
  @GetMapping(value="/create") // http://localhost:9091/member/create
  public String create_form(Model model, @ModelAttribute("memberVO") MemberVO memberVO) {
    return "/member/create";    // /template/member/create.html
  }
  
  /**
   * 회원 가입 처리
   * @param model
   * @param memberVO
   * @return
   */
  @PostMapping(value = "/create")
  public String create_proc(Model model, @ModelAttribute("memberVO") MemberVO memberVO) {
    int checkID_cnt = this.memberProc.checkID(memberVO.getId());
    int checkEMAIL_cnt = this.memberProc.checkEMAIL(memberVO.getEmail());
    
    // id랑 이메일 둘 다 중복 아닐 경우
    if (checkID_cnt == 0 && checkEMAIL_cnt == 0) {
      memberVO.setGrade(11);
      int cnt = this.memberProc.create(memberVO);
      
      if (cnt == 1) {
        model.addAttribute("code", "create_success");
        model.addAttribute("id", "id");
        model.addAttribute("email", memberVO.getEmail());
      } else {
        model.addAttribute("code", "create_fail");
      }
      model.addAttribute("cnt", cnt);
    } else {
      model.addAttribute("code", "duplicate_fail");
      model.addAttribute("cnt", 0);
    }
    
    return "/member/msg";
  }
  
//  /**
//   * 회원가입 처리
//   * @param model
//   * @param memberVO
//   * @return
//   */
//  @PostMapping(value="/create")
//  public String create_proc(HttpSession session, 
//                            Model model,
//                            @ModelAttribute("memberVO") MemberVO memberVO,
//                            RedirectAttributes ra) {
//      int checkID_cnt = this.memberProc.checkID(memberVO.getId());
//      if (checkID_cnt == 0) {
//          if ("admin".equals(memberVO.getId())) {
//              memberVO.setGrade(1); // admin 계정은 GRADE 1로 설정
//              memberVO.setGradeno(null); // gradeno를 NULL로 설정
//          } else {
//              memberVO.setGrade(3); // 기본 회원 3
//              memberVO.setGradeno(3); // 기본 회원 ICON 설정
//          }
//
//          // 기본 이미지 설정
//          memberVO.setPf_img("default.png");
//          memberVO.setFile1saved("default.png");
//          memberVO.setThumb1("default_thumb.png");
//          memberVO.setSize1(0);
//
//          // 기본 이미지 파일 이름을 세션에 저장
//          session.setAttribute("file1saved", "default.png");
//
//          // 회원 등록
//          int cnt = this.memberProc.create(memberVO);
//          if (cnt == 1) {
//              model.addAttribute("code", "create_success");
//              model.addAttribute("name", memberVO.getName());
//              model.addAttribute("id", memberVO.getId());
//              model.addAttribute("gradeno", memberVO.getGradeno());
//          } else {
//              model.addAttribute("code", "create_fail");
//          }
//
//          model.addAttribute("cnt", cnt);
//      } else { // id 중복
//          model.addAttribute("code", "duplicate_fail");
//          model.addAttribute("cnt", 0);
//      }
//
//      return "/member/msg"; // /templates/member/msg.html
//  }
  
  //----------------------------------------------------------------------------------
  // 회원가입 폼 및 처리 메서드 컨트롤러 종료
  // ---------------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------------
  // 회원가입 정보 목록 메서드 컨트롤러 시작 => list_by_memberno_search_paging으로 Update
  // ---------------------------------------------------------------------------------
  
  @GetMapping(value = "/list")
  public String list_memberno_search_paging(Model model, HttpSession session,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "searchType", required = false) String searchType,
      @RequestParam(value = "keyword", defaultValue = "") String keyword) {
    if (this.memberProc.isMemberAdmin(session)) {
      
      // 검색 조건
      SearchDTO searchDTO = new SearchDTO();
      searchDTO.setSearchType(searchType);
      searchDTO.setKeyword(keyword);
      searchDTO.setPage(page);
      searchDTO.setSize(page * 10);
      searchDTO.setOffset((page - 1) * 10);
      
      // 전체 회원 수 조회
      int total = this.memberProc.list_memberno_search_count(searchDTO);
      
      // 검색 페이지 결과가 없고 페이지가 1보다 큰 경우 첫 페이지로 리다이렉트
      if(total == 0 && page > 1) {
        return "redirect:/member/list?searchType=" + searchType + "&keyword=" + keyword;
      }
      
      // 페이징 정보 계산
      PageDTO pageDTO = new PageDTO(total, page);
      
      // 회원 목록 조회
      ArrayList<MemberVO> list = memberProc.list_memberno_search_paging(searchDTO);
      
      model.addAttribute("list", list);
      model.addAttribute("searchDTO", searchDTO);
      model.addAttribute("pageDTO", pageDTO);
      model.addAttribute("total", total);
      
      return "/member/list_memberno_search_paging";
    } else {
      return "redirect:/member/login_cookie_need";
    }
  }
  
  
//  @GetMapping(value = "/list_all")
//  public String list_all(HttpSession session, Model model) {
//    ArrayList<MemberVO> list = this.memberProc.list_all();
//    model.addAttribute("list", list);
//    return "/member/list_all";
//  }
  
//  @GetMapping(value = "/list_by_memberno_search_paging")
//  public String list_by_memberno_search_paging(
//      HttpSession session,
//      Model model,
//      @RequestParam(name = "memberno", defaultValue = "0") int memberno,
//      @RequestParam(name = "id", defaultValue = "") String id,
//      @RequestParam(name = "now_page", defaultValue = "1") int now_page) {
//    
//    int startRow = (now_page - 1) * record_per_page + 1;
//    int endRow = now_page * record_per_page;
//
//    id = Tool.checkNull(id).trim();
//    if (id.isEmpty()) {
//      id = "";
//    }
//    
//    if (memberno < 0) {
//      memberno = 0;
//    }
//    
//    model.addAttribute("memberno", memberno);
//    model.addAttribute("id", id);
//    model.addAttribute("now_page", now_page);
//    
//    HashMap<String, Object> map = new HashMap<>();
//    map.put("memberno", memberno);
//    map.put("id", id);
//    map.put("now_page", now_page);
//    map.put("startRow", startRow);
//    map.put("endRow", endRow);
//    
//    ArrayList<MemberVO> list = this.memberProc.list_by_memberno_search_paging(map);
//    if (list == null || list.isEmpty()) {
//      model.addAttribute("message", "회원이 없습니다.");
//    } else {
//      model.addAttribute("list", list);
//    }
//    
//    int search_count = this.memberProc.list_by_memberno_search_count(map);
//    String paging = this.memberProc.pagingBox(now_page, id, "/member/list_by_memberno_search_paging", search_count, Member.RECORD_PER_PAGE, Member.PAGE_PER_BLOCK);
//    model.addAttribute("paging", paging);
//    model.addAttribute("id", id);
//    model.addAttribute("now_page", now_page);
//    model.addAttribute("search_count", search_count);
//    
//    int no = search_count - ((now_page - 1) * Member.RECORD_PER_PAGE);
//    model.addAttribute("no", no);
//    
//    return "/member/list_by_memberno_search_paging";
//  }
  //----------------------------------------------------------------------------------
  // 회원가입 정보 목록 메서드 컨트롤러 종료
  // ---------------------------------------------------------------------------------

  //----------------------------------------------------------------------------------
  // 회원 정보 조회 메서드 컨트롤러 시작
  // ---------------------------------------------------------------------------------
  
  @GetMapping(value = "/read")
  public String read(HttpSession session, Model model,
      @RequestParam(name = "memberno", required = false) Integer memberno) {
    
    if (this.memberProc.isMember(session) || this.memberProc.isMemberAdmin(session)) {
      // memberno 파라미터가 없는 경우 세션에서 가져옴 (일반 회원인 경우)
      if (memberno == null) {
        memberno = (int) session.getAttribute("memberno"); // session에서 가져오기
      } else {
        // 관리자가 아닌데 다른 회원의 정보를 조회하려는 경우
        if (!this.memberProc.isMemberAdmin(session)) {
          memberno = (int) session.getAttribute("memberno"); // session에서 가져오기
        }
        // 관리자인 경우 전달받은 memberno 사용
      }

      MemberVO memberVO = this.memberProc.read(memberno);
      model.addAttribute("memberVO", memberVO);

      return "/member/read"; // /templates/th/member/read
    } else {
      return "redirect:/member/login_cookie_need";
    }
  }
  
//  /**
//   * 조회
//   * @param model
//   * @param memberno 회원 번호
//   * @return 회원 정보
//   */
//  @GetMapping(value="/read")
//  public String read(HttpSession session, Model model,
//      @RequestParam(name="memberno", defaultValue = "") int memberno) {
//      String grade = (String) session.getAttribute("grade"); // 등급: admin, member, guest
//
//      // grade가 null인지 확인한 후 equals 비교
//      if ("member".equals(grade) && memberno == (int) session.getAttribute("memberno")) {
//          System.out.println("-> read memberno: " + memberno);
//          
//          MemberVO memberVO = this.memberProc.read(memberno);
//          model.addAttribute("memberVO", memberVO);
//          return "member/read";  // templates/member/read.html
//      } else if ("admin".equals(grade)) {
//          System.out.println("-> read memberno: " + memberno);
//          
//          MemberVO memberVO = this.memberProc.read(memberno);
//          model.addAttribute("memberVO", memberVO);
//          
//          return "member/read";  // templates/member/read.html
//      } else {
//          return "redirect:/member/login_cookie_need";  // redirect
//      }
//  }
  //----------------------------------------------------------------------------------
  // 회원 정보 조회 메서드 컨트롤러 종료
  // ---------------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------------
  // 회원 정보 수정 메서드 컨트롤러 시작
  // ---------------------------------------------------------------------------------
  
  /**
   * 회원 정보 수정 폼(회원, 관리자 본인 계정만 수정 가능)
   * @param session
   * @param model
   * @param email
   * @param memberno
   * @return
   */
  @GetMapping(value = "/update")
  public String update_form(HttpSession session, Model model,
      @RequestParam(name = "email", required = false) String email,
      @RequestParam(name = "memberno", required = false) Integer memberno) {
    
    // 회원은 회원 등급만 처리
    memberno = (int) session.getAttribute("memberno");
    
    if (this.memberProc.isMember(session) && memberno == (int) session.getAttribute("memberno")) {
      System.out.println("memberno: " + memberno);
      MemberVO memberVO = this.memberProc.read(memberno);
      model.addAttribute("memberVO", memberVO);
      
      return "/member/update";
      
    } else if (this.memberProc.isMemberAdmin(session) && memberno == (int) session.getAttribute("memberno")) {
      System.out.println("-> admin memberno: " + memberno);
      MemberVO memberVO = this.memberProc.read(memberno);
      
      model.addAttribute("memberVO", memberVO);
      
      return "/member/update";
    } else {
      return "redirect:/member/login_cookie_nedd";
    }
  }
  
  /**
   * 회원 정보 수정 처리(회원, 관리자 본인 계정만 수정 가능)
   * @param session
   * @param model
   * @param memberVO
   * @return
   */
  @PostMapping(value = "/update")
  public String update_proc(HttpSession session, Model model,
      @Validated(UpdateValidationGroup.class) @ModelAttribute("memberVO") MemberVO memberVO) {
    
    String emailSession = (String) session.getAttribute("email");
    
    // 회원 본인일 때
    if (this.memberProc.isMember(session) && memberVO.getMemberno() == (int) session.getAttribute("memberno")) {
      int checkEMAIL_cnt = this.memberProc.checkEMAIL(memberVO.getEmail());
      // 이메일 중복되지 않았을 경우
      if (checkEMAIL_cnt == 0) {
        memberVO.setMemberno(memberVO.getMemberno());
        memberVO.setEmail(memberVO.getEmail().trim());
        memberVO.setName(memberVO.getName().trim());
        memberVO.setNickname(memberVO.getNickname().trim());
        memberVO.setBirth(memberVO.getBirth().trim());
        memberVO.setZipcode(memberVO.getZipcode().trim());
        memberVO.setAddress1(memberVO.getAddress1().trim());
        memberVO.setAddress2(memberVO.getAddress2().trim());
        
        int cnt = this.memberProc.update(memberVO);
        System.out.println("update cnt1: " + cnt);
        
        if (cnt == 1) {
          model.addAttribute("code", "update_success");
          model.addAttribute("memberno", memberVO.getMemberno());
          model.addAttribute("email", memberVO.getEmail());
          model.addAttribute("name", memberVO.getName());
          model.addAttribute("nickname", memberVO.getNickname());
          model.addAttribute("birth", memberVO.getBirth());
          model.addAttribute("zipcode", memberVO.getZipcode());
          model.addAttribute("address1", memberVO.getAddress1());
          model.addAttribute("address2", memberVO.getAddress2());
          
        } else {
          model.addAttribute("code", "update_fail");
          System.out.println("update_fail");
        }
        
        model.addAttribute("cnt", cnt);
        System.out.println("update_success_cnt: " + cnt);
      }
      // 이메일 중복됐지만 기존 회원 이메일이랑 같을 경우
      else if (checkEMAIL_cnt == 1 && emailSession.equals(memberVO.getEmail())) {
        memberVO.setMemberno(memberVO.getMemberno());
        memberVO.setEmail(memberVO.getEmail().trim());
        memberVO.setName(memberVO.getName().trim());
        memberVO.setNickname(memberVO.getNickname().trim());
        memberVO.setBirth(memberVO.getBirth().trim());
        memberVO.setZipcode(memberVO.getZipcode().trim());
        memberVO.setAddress1(memberVO.getAddress1().trim());
        memberVO.setAddress2(memberVO.getAddress2().trim());
        
        int cnt = this.memberProc.update(memberVO);
        System.out.println("update cnt: " + cnt);
        
        if (cnt == 1) {
          model.addAttribute("code", "update_success");
          model.addAttribute("memberno", memberVO.getMemberno());
          model.addAttribute("email", memberVO.getEmail());
          model.addAttribute("name", memberVO.getName());
          model.addAttribute("nickname", memberVO.getNickname());
          model.addAttribute("birth", memberVO.getBirth());
          model.addAttribute("zipcode", memberVO.getZipcode());
          model.addAttribute("address1", memberVO.getAddress1());
          model.addAttribute("address2", memberVO.getAddress2());
          
        } else {
          model.addAttribute("code", "update_fail");
          System.out.println("update_fail");
        }
        
        model.addAttribute("cnt", cnt);
        System.out.println("update_success_cnt: " + cnt);
      }
      return "redirect:/member/read";
    } // 관리자 본인일 경우
    else if (this.memberProc.isMemberAdmin(session) && memberVO.getMemberno() == (int) session.getAttribute("memberno")) {
      int checkEMAIL_cnt = this.memberProc.checkEMAIL(memberVO.getEmail());
      // 이메일 중복되지 않았을 경우
      if (checkEMAIL_cnt == 0) {
        memberVO.setMemberno(memberVO.getMemberno());
        memberVO.setEmail(memberVO.getEmail().trim());
        memberVO.setName(memberVO.getName().trim());
        memberVO.setNickname(memberVO.getNickname().trim());
        memberVO.setBirth(memberVO.getBirth().trim());
        memberVO.setZipcode(memberVO.getZipcode().trim());
        memberVO.setAddress1(memberVO.getAddress1().trim());
        memberVO.setAddress2(memberVO.getAddress2().trim());
        
        int cnt = this.memberProc.update(memberVO);
        System.out.println("update cnt: " + cnt);
        
        if (cnt == 1) {
          model.addAttribute("code", "update_success");
          model.addAttribute("memberno", memberVO.getMemberno());
          model.addAttribute("email", memberVO.getEmail());
          model.addAttribute("name", memberVO.getName());
          model.addAttribute("nickname", memberVO.getNickname());
          model.addAttribute("birth", memberVO.getBirth());
          model.addAttribute("zipcode", memberVO.getZipcode());
          model.addAttribute("address1", memberVO.getAddress1());
          model.addAttribute("address2", memberVO.getAddress2());
          
        } else {
          model.addAttribute("code", "update_fail");
          System.out.println("update_fail");
        }
        
        model.addAttribute("cnt", cnt);
        System.out.println("update_success_cnt: " + cnt);
      }
      // 이메일 중복됐지만 기존 회원 이메일이랑 같을 경우
      else if (checkEMAIL_cnt == 1 && emailSession.equals(memberVO.getEmail())) {
        memberVO.setMemberno(memberVO.getMemberno());
        memberVO.setEmail(memberVO.getEmail().trim());
        memberVO.setName(memberVO.getName().trim());
        memberVO.setNickname(memberVO.getNickname().trim());
        memberVO.setBirth(memberVO.getBirth().trim());
        memberVO.setZipcode(memberVO.getZipcode().trim());
        memberVO.setAddress1(memberVO.getAddress1().trim());
        memberVO.setAddress2(memberVO.getAddress2().trim());
        
        int cnt = this.memberProc.update(memberVO);
        System.out.println("update cnt: " + cnt);
        
        if (cnt == 1) {
          model.addAttribute("code", "update_success");
          model.addAttribute("memberno", memberVO.getMemberno());
          model.addAttribute("email", memberVO.getEmail());
          model.addAttribute("name", memberVO.getName());
          model.addAttribute("nickname", memberVO.getNickname());
          model.addAttribute("birth", memberVO.getBirth());
          model.addAttribute("zipcode", memberVO.getZipcode());
          model.addAttribute("address1", memberVO.getAddress1());
          model.addAttribute("address2", memberVO.getAddress2());
          
        } else {
          model.addAttribute("code", "update_fail");
          System.out.println("update_fail");
        }
        
        model.addAttribute("cnt", cnt);
        System.out.println("update_success_cnt: " + cnt);
      }
      return "redirect:/member/read";
    } else {
      return "redirect:/member/login_cookie_need";
    }
  }
  
  /**
   * 비밀번호 수정 폼
   * @param session
   * @param model
   * @return
   */
  @GetMapping(value = "/passwd_update")
  public String passwd_update_form(HttpSession session, Model model) {
    
    if (this.memberProc.isMember(session)) {
      int memberno = (int) session.getAttribute("memberno");
      
      MemberVO memberVO = this.memberProc.read(memberno);
      
      model.addAttribute("memberVO", memberVO);
      
      return "/member/passwd_update";
    } else if (this.memberProc.isMemberAdmin(session)) {
      int memberno = (int) session.getAttribute("memberno");
      
      MemberVO memberVO = this.memberProc.read(memberno);
      
      model.addAttribute("memberVO", memberVO);
      
      return "/member/passwd_update";
    } else {
      return "redirect:/member/login_cookie_need";
    }
  }
  
  /**
   * 현재 비밀번호 확인
   * @param session
   * @param json_src
   * @return
   */
  @PostMapping(value = "/passwd_check")
  @ResponseBody
  public String passwd_check(HttpSession session, @RequestBody String json_src) {
    System.out.println("json_src: " + json_src);
    
    JSONObject src = new JSONObject(json_src);
    
    String current_passwd = (String) src.get("current_passwd");
    System.out.println("-> current_passwd: " + current_passwd);
    
    int memberno = (int) session.getAttribute("memberno");
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("memberno", memberno);
    map.put("passwd", current_passwd);
    
    int cnt = this.memberProc.passwd_check(map);
    
    JSONObject json = new JSONObject();
    json.put("cnt", cnt);
    System.out.println(json.toString());
    
    return json.toString();
  }
  
  /**
   * 비밀번호 수정 처리
   * @param session
   * @param model
   * @param current_passwd
   * @param passwd
   * @return
   */
  @PostMapping(value = "/passwd_update")
  public String passwd_update_proc(HttpSession session, Model model,
      @RequestParam(value = "current_passwd", defaultValue = "") String current_passwd,
      @RequestParam(value = "passwd", defaultValue = "") String passwd) {
    
    if (this.memberProc.isMember(session)) {
      int memberno = (int) session.getAttribute("memberno");
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("memberno", memberno);
      map.put("passwd", current_passwd);
      
      int cnt = this.memberProc.passwd_check(map);
      
      if (cnt == 0) {
        model.addAttribute("code", "passwd_not_equal");
        model.addAttribute("cnt", 0);
      } else {
        map = new HashMap<String, Object>();
        map.put("memberno", memberno);
        map.put("passwd", passwd);
        
        int passwd_change_cnt = this.memberProc.passwd_update(map);
        
        if (passwd_change_cnt == 1) {
          model.addAttribute("code", "passwd_change_success");
          model.addAttribute("cnt", 1);
        } else {
          model.addAttribute("code", "passwd_change_success");
          model.addAttribute("cnt", 0);
        }
      }
      
      return "redirect:/member/read";
    } else {
      return "redirect:/member/login_cookie_need";
    }
  }
  
  /**
   * 회원 탈퇴 폼
   * @param session
   * @param model
   * @param memberno
   * @return
   */
  @GetMapping(value = "/unsub_delete")
  public String unsub_delete_form(HttpSession session, Model model,
      @RequestParam(name = "memberno", required = false) Integer memberno) {
    
    if (this.memberProc.isMember(session)) {
      memberno = (int) session.getAttribute("memberno");
      MemberVO memberVO = this.memberProc.read(memberno);
      model.addAttribute("memberVO", memberVO);
      
      return "/member/unsub_delete";
    } else if (this.memberProc.isMemberAdmin(session)) {
      memberno = (int) session.getAttribute("memberno");
      MemberVO memberVO = this.memberProc.read(memberno);
      model.addAttribute("memberVO", memberVO);
      
      return "/member/unsub_delete";
    } else {
      return "redirect:/member/login_cookie_need";
    }
  }
  
  /**
   * 회원 탈퇴 처리
   * @param session
   * @param model
   * @param memberVO
   * @return
   */
  @PostMapping(value = "/unsub_delete")
  public String unsub_delete_process(HttpSession session, Model model, @ModelAttribute("memberVO") MemberVO memberVO) {
    
    // 회원 및 회원 본인일 경우
    if (this.memberProc.isMember(session) && memberVO.getMemberno() == (int) session.getAttribute("memberno")) {
      int cnt = this.memberProc.unsub_delete(memberVO);
      
      if (cnt == 1) {
        model.addAttribute("code", "unsub_delete_success");
        model.addAttribute("memberno", memberVO.getMemberno());
        model.addAttribute("grade", memberVO.getGrade());
        
        System.out.println("memberno: " + memberVO.getMemberno() + ", id: " + memberVO.getId() + "email: " + memberVO.getEmail() + " has unsub_delete");
        
        session.invalidate();
        return "redirect:/";
      } else {
        model.addAttribute("code", "unsub_delete_fail");
        return "/member/error";
      }
    }
    // 관리자 및 관리자 본인일 경우
    else if (this.memberProc.isMemberAdmin(session) && memberVO.getMemberno() == (int) session.getAttribute("memberno")) {
      int cnt = this.memberProc.unsub_delete(memberVO);
      
      if (cnt == 1) {
        model.addAttribute("code", "unsub_delete_success");
        model.addAttribute("memberno", memberVO.getMemberno());
        model.addAttribute("grade", memberVO.getGrade());
        
        System.out.println("memberno: " + memberVO.getMemberno() + ", id: " + memberVO.getId() + "email: " + memberVO.getEmail() + " has unsub_delete");
        
        session.invalidate();
        return "redirect:/";
      } else {
        model.addAttribute("code", "unsub_delete_fail");
        return "/member/error";
      }
    } else {
      return "redirect:/member/login_cookie_need";
    }
  }
  
//  /**
//   * 수정 처리
//   * @param model
//   * @param memberVO
//   * @return
//   */
//  @PostMapping(value="/update")
//  public String update_proc(HttpSession session, Model model, 
//      @ModelAttribute("memberVO") MemberVO memberVO) {
//    
//    String grade = (String)session.getAttribute("grade"); // 등급: admin, member, guest
//
//    // 회원 본인이거나 관리자인경우 처리
//    if ((grade.equals("member") &&  memberVO.getMemberno() == (int)session.getAttribute("memberno")) ||  grade.equals("admin")) {
//      int cnt = this.memberProc.update(memberVO); // 수정
//      
//      if (cnt == 1) {
//        model.addAttribute("code", "update_success");
//        model.addAttribute("name", memberVO.getName());
//        model.addAttribute("id", memberVO.getId());
//      } else {
//        model.addAttribute("code", "update_fail");
//      }
//      
//      model.addAttribute("cnt", cnt);
//      
//      return "/member/msg"; // /templates/member/msg.html
//    } else {
//      return "redirect:/member/login_cookie_need";  // redirect
//    }
//    
//  }
  //----------------------------------------------------------------------------------
  // 회원 정보 수정 메서드 컨트롤러 종료
  // ---------------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------------
  // 회원 정보 삭제 메서드 컨트롤러 시작
  // ---------------------------------------------------------------------------------
//  /**
//   * 삭제 폼
//   * @param model
//   * @param memberno 회원 번호
//   * @return 회원 정보
//   */
//  @GetMapping(value="/delete")
//  public String delete(HttpSession session,
//                              Model model,
//                              @RequestParam(name="memberno", defaultValue = "") int memberno) {
//    if (this.memberProc.isAdmin(session)) {
//      System.out.println("-> delete memberno: " + memberno);
//      
//      MemberVO memberVO = this.memberProc.read(memberno);
//      model.addAttribute("memberVO", memberVO);
//      
//      return "/member/delete";  // templates/member/delete.html
//    } else {
//      return "redirect:/member/login_cookie_need";  // redirect
//    }
//    
//  }
//  
//  /**
//   * 삭제 처리
//   * @param model
//   * @param memberno 삭제할 레코드 번호
//   * @return
//   */
//  @PostMapping(value="/delete")
//  public String delete_process(HttpSession session,
//                                          Model model,
//                                          @RequestParam(name="memberno", defaultValue = "") int memberno) {
//    if (this.memberProc.isAdmin(session)) {
//      int cnt = this.memberProc.delete(memberno);
//      
//      if (cnt == 1) {
//        return "redirect:/member/list_all";
//      } else {
//        model.addAttribute("code", "delete_fail");
//        return "/member/msg"; // /templates/member/msg.html
//      }
//    } else {
//      return "redirect:/member/login_cookie_need";  // redirect
//    }
//  }
  //----------------------------------------------------------------------------------
  // 회원 정보 삭제 메서드 컨트롤러 종료
  // ---------------------------------------------------------------------------------
  
//  
//  /**
//   * 유형 3
//   * 카테고리별 목록 + 검색 + 페이징 http://localhost:9093/member/list_by_memberno?memberno=5
//   * http://localhost:9093/member/list_by_memberno?memberno=6
//   * 
//   * @return
//   */
//  @GetMapping(value = "/list_by_memberno_search_paging")
//  public String list_by_memberno_search_paging(
//      HttpSession session, 
//      Model model, 
//      @RequestParam(name = "memberno", defaultValue = "0") int memberno,
//      @RequestParam(name = "word", defaultValue = "") String word,
//      @RequestParam(name = "now_page", defaultValue = "1") int now_page) {
//
//      // 세션에서 등급 확인
//      String grade = (String) session.getAttribute("grade");
//
//      // 관리자 등급만 접근 허용
//      if (grade != null && grade.equals("admin")) {
//          MemberVO memberVO = this.memberProc.read(memberno);
//          model.addAttribute("memberVO", memberVO);
//
//          word = Tool.checkNull(word).trim();
//
//          HashMap<String, Object> map = new HashMap<>();
//          map.put("memberno", memberno);
//          map.put("word", word);
//          map.put("now_page", now_page);
//
//          ArrayList<MemberVO> list = this.memberProc.list_by_memberno_search_paging(map);
//          model.addAttribute("list", list);
//
//          model.addAttribute("word", word);
//
//          int search_count = this.memberProc.list_by_memberno_search_count(map);
//          String paging = this.memberProc.pagingBox(memberno, now_page, word, "/member/list_by_memberno", search_count,
//              Member.RECORD_PER_PAGE, Member.PAGE_PER_BLOCK);
//          model.addAttribute("paging", paging);
//          model.addAttribute("now_page", now_page);
//
//          model.addAttribute("search_count", search_count);
//
//          // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
//          int no = search_count - ((now_page - 1) * Member.RECORD_PER_PAGE);
//          model.addAttribute("no", no);
//
//          return "/member/list_by_memberno_search_paging"; // /templates/member/list_by_memberno_search_paging.html
//      } else {
//          return "redirect:/member/login_cookie_need"; // redirect
//      }
//  }

//  
//  /**
//   * 프로필 이미지 수정 폼
//   * @param session
//   * @param model
//   * @param memberno
//   * @param word
//   * @param now_page
//   * @return
//   */
//  @GetMapping(value = "/update_file")
//  public String update_file(HttpSession session, Model model, 
//      @RequestParam(name="memberno", defaultValue="0") int memberno) {
//      
//      MemberVO memberVO = this.memberProc.read(memberno);
//      model.addAttribute("memberVO", memberVO);
//
//      return "/member/update_file";
//  }
//  
//  /**
//   * 프로필 이미지 수정 처리
//   * @param session
//   * @param model
//   * @param ra
//   * @param memberVO
//   * @param word
//   * @param now_page
//   * @return
//   */
//  @PostMapping(value = "/update_file")
//  public String update_file(HttpSession session, 
//      Model model, 
//      RedirectAttributes ra,
//      @ModelAttribute MemberVO memberVO) {
//
//      String upDir = Member.getUploadDir(); // 파일을 업로드할 폴더 준비
//      MultipartFile mf = memberVO.getFile1MF();
//      String file1 = mf.getOriginalFilename();
//      long size1 = mf.getSize();
//
//      if (size1 > 0) { // 파일이 업로드된 경우
//          String file1saved = Upload.saveFileSpring(mf, upDir);
//          String thumb1 = "";
//          if (Tool.isImage(file1saved)) { // 이미지인지 검사
//              thumb1 = Tool.preview(upDir, file1saved, 200, 150);
//          }
//          memberVO.setPf_img(file1);
//          memberVO.setFile1saved(file1saved);
//          memberVO.setThumb1(thumb1);
//          memberVO.setSize1(size1);
//      } else { // 파일이 업로드되지 않은 경우
//          MemberVO oldMemberVO = this.memberProc.read(memberVO.getMemberno());
//          memberVO.setFile1saved(oldMemberVO.getFile1saved());
//          memberVO.setThumb1(oldMemberVO.getThumb1());
//          memberVO.setSize1(oldMemberVO.getSize1());
//      }
//
//      int cnt = this.memberProc.update(memberVO);
//      if (cnt == 1) {
//          ra.addFlashAttribute("code", "update_success");
//      } else {
//          ra.addFlashAttribute("code", "update_fail");
//      }
//      return "redirect:/member/list";
//  }
  
//  /**
//   * 프로필 폼
//   * @param session
//   * @param model
//   * @return
//   */
//  @GetMapping(value = "/mypage")
//  public String mypageForm(HttpSession session, Model model) {
//    Integer memberno = (Integer) session.getAttribute("memberno");
//    if (memberno == null) {
//      return "redirect:/member/login";
//    }
//
//    MemberVO memberVO = this.memberProc.read(memberno);
//    if (memberVO == null) {
//      return "redirect:/member/login";
//    }
//    model.addAttribute("memberVO", memberVO);
//    return "/member/mypage"; // mypage.html로 이동
//  }
//
//  /**
//   * 프로필 처리
//   * @param session
//   * @param model
//   * @param memberVO
//   * @param currentPasswd
//   * @param newPasswd
//   * @param ra
//   * @return
//   */
//  @PostMapping(value = "/mypage")
//  public String mypageProc(HttpSession session,
//                           Model model,
//                           @ModelAttribute("memberVO") MemberVO memberVO,
//                           @RequestParam(value="current_passwd", defaultValue="") String currentPasswd,
//                           @RequestParam(value="new_passwd", defaultValue="") String newPasswd,
//                           RedirectAttributes ra) {
//    int memberno = (int) session.getAttribute("memberno");
//    HashMap<String, Object> map = new HashMap<>();
//    map.put("memberno", memberno);
//    map.put("passwd", currentPasswd);
//
//    int passwdCheck = this.memberProc.passwd_check(map);
//    if (passwdCheck == 0) {
//      ra.addFlashAttribute("code", "passwd_not_equal");
//      return "redirect:/member/mypage";
//    }
//
//    String upDir = Member.getUploadDir();
//    MultipartFile mf = memberVO.getFile1MF();
//    String file1 = mf.getOriginalFilename();
//    long size1 = mf.getSize();
//
//    if (size1 > 0) {
//      String file1saved = Upload.saveFileSpring(mf, upDir);
//      String thumb1 = "";
//      if (Tool.isImage(file1saved)) {
//        thumb1 = Tool.preview(upDir, file1saved, 200, 150);
//      }
//      memberVO.setPf_img(file1);
//      memberVO.setFile1saved(file1saved);
//      memberVO.setThumb1(thumb1);
//      memberVO.setSize1(size1);
//      session.setAttribute("file1saved", file1saved);
//    } else {
//      MemberVO oldMemberVO = this.memberProc.read(memberno);
//      memberVO.setPf_img(oldMemberVO.getPf_img());
//      memberVO.setFile1saved(oldMemberVO.getFile1saved());
//      memberVO.setThumb1(oldMemberVO.getThumb1());
//      memberVO.setSize1(oldMemberVO.getSize1());
//      session.setAttribute("file1saved", oldMemberVO.getFile1saved());
//    }
//
//    if (!newPasswd.isEmpty()) {
//      memberVO.setPasswd(newPasswd);
//    } else {
//      memberVO.setPasswd(currentPasswd);
//    }
//
//    int cnt = this.memberProc.update(memberVO);
//    if (cnt == 1) {
//      ra.addFlashAttribute("code", "update_success");
//    } else {
//      ra.addFlashAttribute("code", "update_fail");
//    }
//    return "redirect:/member/mypage";
//  }
//  
//  /**
//   * 개인정보수정 폼
//   * @param session
//   * @param model
//   * @return
//   */
//  @GetMapping(value = "/update_information")
//  public String update_information_form(HttpSession session, Model model) {
//      Integer memberno = (Integer) session.getAttribute("memberno");
//      if (memberno == null) {
//          return "redirect:/member/login";
//      }
//
//      MemberVO memberVO = this.memberProc.read(memberno);
//      if (memberVO == null) {
//          return "redirect:/member/login";
//      }
//      model.addAttribute("memberVO", memberVO);
//      return "/member/update_information"; // update_information.html로 이동
//  }
//  
//  /**
//   * 개인정보수정 처리
//   * @param session
//   * @param model
//   * @param memberVO
//   * @param ra
//   * @return
//   */
//  @PostMapping(value = "/update_information")
//  public String update_information_proc(HttpSession session, 
//                                        Model model,
//                                        @ModelAttribute("memberVO") MemberVO memberVO,
//                                        RedirectAttributes ra) {
//      int memberno = (int) session.getAttribute("memberno");
//      memberVO.setMemberno(memberno);
//
//      int cnt = this.memberProc.update(memberVO);
//      if (cnt == 1) {
//          ra.addFlashAttribute("code", "update_success");
//      } else {
//          ra.addFlashAttribute("code", "update_fail");
//      }
//      return "redirect:/member/update_information";
//  }
  
//  /**
//   * 문의글 폼
//   * @param session
//   * @param model
//   * @return
//   */
//  @GetMapping(value = "/inquiries")
//  public String inquiriesForm(HttpSession session, Model model) {
//    int memberno = (int) session.getAttribute("memberno");
//    HashMap<String, Object> map = new HashMap<>();
//    map.put("memberno", memberno);
//    ArrayList<BoardVO> inquiries = this.boardProc.list_by_boardno_search(map);
//    model.addAttribute("inquiries", inquiries);
//    return "/member/inquiries"; // inquiries.html로 이동
//  }
  
//  /**
//   * 회원 탈퇴 폼
//   * @param session
//   * @param model
//   * @return
//   */
//  @GetMapping(value = "/delete_account")
//  public String deleteAccountForm(HttpSession session, Model model) {
//    return "/member/delete_account"; // delete_account.html로 이동
//  }
//
//  /**
//   * 회원 탈퇴 처리
//   * @param session
//   * @param model
//   * @return
//   */
//  @PostMapping(value = "/delete_account")
//  public String deleteAccount(HttpSession session, Model model) {
//    int memberno = (int) session.getAttribute("memberno"); // 현재 로그인한 회원의 번호를 가져옴
//    this.memberProc.delete(memberno); // 회원 탈퇴 처리
//    session.invalidate(); // 세션 무효화
//    
//    return "redirect:/"; // 메인 페이지로 리다이렉트
//  }
//
//  

  //----------------------------------------------------------------------------------
  // 로그인 및 로그아웃 메서드 컨트롤러 시작
  // ---------------------------------------------------------------------------------

  // ----------------------------------------------------------------------------------
  // Cookie 사용 로그인 관련 코드 시작
  // ----------------------------------------------------------------------------------
  /**
   * 로그인
   * @param model
   * @param memberno 회원 번호
   * @return 회원 정보
   */
  @GetMapping(value="/login")
  public String login_form(Model model, HttpServletRequest request) {
    
    // Cookie 관련 코드---------------------------------------------------------
    Cookie[] cookies = request.getCookies();
    Cookie cookie = null;
  
    String ck_id = ""; // id 저장
    String ck_id_save = ""; // id 저장 여부를 체크
    String ck_passwd = ""; // passwd 저장
    String ck_passwd_save = ""; // passwd 저장 여부를 체크
  
    if (cookies != null) { // 쿠키가 존재한다면
      for (int i=0; i < cookies.length; i++){
        cookie = cookies[i]; // 쿠키 객체 추출
      
        if (cookie.getName().equals("ck_id")){                     // 아이디
          ck_id = cookie.getValue();  // email
        }else if(cookie.getName().equals("ck_id_save")){        // 아이디 저장 여부
          ck_id_save = cookie.getValue();  // Y, N
        }else if (cookie.getName().equals("ck_passwd")){       // 패스워드
          ck_passwd = cookie.getValue();         // 1234
        }else if(cookie.getName().equals("ck_passwd_save")){ // 패스워드 저장 여부
          ck_passwd_save = cookie.getValue();  // Y, N
        }
      }
    }
    // ----------------------------------------------------------------------------
    model.addAttribute("ck_id", ck_id);
    model.addAttribute("ck_id_save", ck_id_save);
    model.addAttribute("ck_passwd", ck_passwd);
    model.addAttribute("ck_passwd_save", ck_passwd_save);
    
    return "/member/login_cookie";  // /templates/member/login_cookie.html
  }

  /**
   * Cookie 기반 로그인 처리
   * @param session
   * @param request
   * @param response
   * @param model
   * @param id 아이디
   * @param passwd 패스워드
   * @param id_save 아이디 저장 여부
   * @param passwd_save 패스워드 저장 여부
   * @return
   */
  @PostMapping(value="/login")
  public String login_proc(HttpSession session,
      HttpServletRequest request,
      HttpServletResponse response,
      Model model,
      RedirectAttributes ra,
      @RequestParam(value="id", defaultValue = "") String id, 
      @RequestParam(value="passwd", defaultValue = "") String passwd,
      @RequestParam(value="id_save", defaultValue = "") String id_save,
      @RequestParam(value="passwd_save", defaultValue = "") String passwd_save) {
    
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("id", id);
    map.put("passwd", passwd);
    
    int cnt = this.memberProc.login(map);
    System.out.println("-> login_proc cnt: " + cnt);
    
//    // 로그인 시도한 IP 주소 가져오기
//    String ip = request.getRemoteAddr();
//    
//    // 로그인 로그를 기록하기 위한 객체 생성
//    LoginlogVO loginlogVO = new LoginlogVO();
//    loginlogVO.setId(id);
//    loginlogVO.setIp(ip);
//    loginlogVO.setResult(cnt == 1 ? "T" : "F"); // 로그인 성공 여부
//    
//    // 로그인 기록 저장
//    this.loginlogProc.login_log(loginlogVO);
//    
    
    
    if (cnt == 1) {
      
      // id를 이용하여 회원 정보 조회
      MemberVO memberVO = this.memberProc.readById(id);
      
      if(memberVO.getGrade() == 99) {
        ra.addFlashAttribute("cnt", 0);
        ra.addFlashAttribute("code", "unsub_delete member");
        return "redirect:/member/login";
      }
      
      session.setAttribute("memberno", memberVO.getMemberno());
      session.setAttribute("id", memberVO.getId());
      session.setAttribute("name", memberVO.getName());
      session.setAttribute("email", memberVO.getEmail());
      session.setAttribute("grade", memberVO.getGrade());
      session.setAttribute("file1saved", memberVO.getFile1saved()); // 프로필 이미지 파일 이름 저장
      
      if (memberVO.getGrade() >= 1 && memberVO.getGrade() <= 10) {
        session.setAttribute("grade", "admin");
      } else if (memberVO.getGrade() >= 11 && memberVO.getGrade() <= 20) {
        session.setAttribute("grade", "member");
      } else if (memberVO.getGrade() >= 41 && memberVO.getGrade() <= 49) {
        session.setAttribute("grade", "stopped");
      }
//      if (memberVO.getGradeno() == null) {
//        session.setAttribute("grade", "admin");
//      } else if (memberVO.getGradeno() >= 3 && memberVO.getGradeno() <= 16) {
//        session.setAttribute("grade", "member"); // 기본 회원
//      } else if (memberVO.getGrade() >= 21) {
//        session.setAttribute("grade", "guest");
//      }
      
      // Cookie 관련 코드---------------------------------------------------------
      // -------------------------------------------------------------------
      // id 관련 쿠기 저장
      // -------------------------------------------------------------------
      if (id_save.equals("Y")) { // id를 저장할 경우, Checkbox를 체크한 경우
        Cookie ck_id = new Cookie("ck_id", id);
        ck_id.setPath("/");  // root 폴더에 쿠키를 기록함으로 모든 경로에서 쿠기 접근 가능
        ck_id.setMaxAge(60 * 60 * 24 * 30); // 30 day, 초단위
        response.addCookie(ck_id); // id 저장
      } else { // N, id를 저장하지 않는 경우, Checkbox를 체크 해제한 경우
        Cookie ck_id = new Cookie("ck_id", "");
        ck_id.setPath("/");
        ck_id.setMaxAge(0); // 0초
        response.addCookie(ck_id); // id 저장
      }
      
      // id를 저장할지 선택하는  CheckBox 체크 여부
      Cookie ck_id_save = new Cookie("ck_id_save", id_save);
      ck_id_save.setPath("/");
      ck_id_save.setMaxAge(60 * 60 * 24 * 30); // 30 day
      response.addCookie(ck_id_save);
      // -------------------------------------------------------------------
  
      // -------------------------------------------------------------------
      // Password 관련 쿠기 저장
      // -------------------------------------------------------------------
      if (passwd_save.equals("Y")) { // 패스워드 저장할 경우
        Cookie ck_passwd = new Cookie("ck_passwd", passwd);
        ck_passwd.setPath("/");
        ck_passwd.setMaxAge(60 * 60 * 24 * 30); // 30 day
        response.addCookie(ck_passwd);
      } else { // N, 패스워드를 저장하지 않을 경우
        Cookie ck_passwd = new Cookie("ck_passwd", "");
        ck_passwd.setPath("/");
        ck_passwd.setMaxAge(0);
        response.addCookie(ck_passwd);
      }
      // passwd를 저장할지 선택하는  CheckBox 체크 여부
      Cookie ck_passwd_save = new Cookie("ck_passwd_save", passwd_save);
      ck_passwd_save.setPath("/");
      ck_passwd_save.setMaxAge(60 * 60 * 24 * 30); // 30 day
      response.addCookie(ck_passwd_save);
      // -------------------------------------------------------------------
      // ----------------------------------------------------------------------------     
      
      return "redirect:/";
    } else { // 로그인 실패
      ra.addFlashAttribute("cnt", cnt);
      model.addAttribute("code", "login_fail");
      System.out.println("login_fail");
      return "redirect:/member/login";
//      return "/member/msg";
    }
  }
  
  /**
   * 로그아웃
   * @param model
   * @param memberno 회원 번호
   * @return 회원 정보
   */
  @GetMapping(value="/logout")
  public String logout(HttpSession session, Model model) {
    session.invalidate();  // 모든 세션 변수 삭제
    return "redirect:/";
  }
  
  // ----------------------------------------------------------------------------------
  // Cookie 사용 로그인 관련 코드 종료
  // ----------------------------------------------------------------------------------

  /**
   * 로그인 요구에 따른 로그인 폼 출력 
   * @param model
   * @param memberno 회원 번호
   * @return 회원 정보
   */
  @GetMapping(value="/login_cookie_need")
  public String login_cookie_need(Model model, HttpServletRequest request) {
    // Cookie 관련 코드---------------------------------------------------------
    Cookie[] cookies = request.getCookies();
    Cookie cookie = null;
  
    String ck_id = ""; // id 저장
    String ck_id_save = ""; // id 저장 여부를 체크
    String ck_passwd = ""; // passwd 저장
    String ck_passwd_save = ""; // passwd 저장 여부를 체크
  
    if (cookies != null) { // 쿠키가 존재한다면
      for (int i=0; i < cookies.length; i++){
        cookie = cookies[i]; // 쿠키 객체 추출
      
        if (cookie.getName().equals("ck_id")){
          ck_id = cookie.getValue();  // email
        }else if(cookie.getName().equals("ck_id_save")){
          ck_id_save = cookie.getValue();  // Y, N
        }else if (cookie.getName().equals("ck_passwd")){
          ck_passwd = cookie.getValue();         // 1234
        }else if(cookie.getName().equals("ck_passwd_save")){
          ck_passwd_save = cookie.getValue();  // Y, N
        }
      }
    }
    // ----------------------------------------------------------------------------
    model.addAttribute("ck_id", ck_id);
    model.addAttribute("ck_id_save", ck_id_save);
    model.addAttribute("ck_passwd", ck_passwd);
    model.addAttribute("ck_passwd_save", ck_passwd_save);
    return "/member/login_cookie_need";  // templates/member/login_cookie_need.html
  }
  
  //----------------------------------------------------------------------------------
  // 로그인 및 로그아웃 메서드 컨트롤러 종료
  // ---------------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------------
  // 패스워드 수정 및 변경 메서드 컨트롤러 시작
  // ---------------------------------------------------------------------------------
//  /**
//  * 패스워드 수정 폼
//  * @param model
//  * @param memberno
//  * @return
//  */
//  @GetMapping(value="/passwd_update")
//  public String passwd_update_form(HttpSession session, Model model) {
//  // ArrayList<MemberVOMenu> menu = this.memberProc.menu();
//  // model.addAttribute("menu", menu);
//   
//   if (this.memberProc.isMember(session)) {
//     int memberno = (int)session.getAttribute("memberno"); // session에서 가져오기
//     
//     MemberVO memberVO = this.memberProc.read(memberno);
//  
//     model.addAttribute("memberVO", memberVO);
//     
//     return "/member/passwd_update";    // /templates/member/passwd_update.html      
//   } else {
//     return "redirect:/member/login_cookie_need"; // redirect
//   }
//  
//  }
//  
//  /**
//  * 현재 패스워드 확인
//  * @param session
//  * @param current_passwd
//  * @return 1: 일치, 0: 불일치
//  */
//  @PostMapping(value="/passwd_check")
//  @ResponseBody
//  public String passwd_check(HttpSession session, @RequestBody String json_src) {
//   System.out.println("-> json_src: " + json_src); // json_src: {"current_passwd":"1234"}
//   
//   JSONObject src = new JSONObject(json_src); // String -> JSON
//   
//   String current_passwd = (String)src.get("current_passwd"); // 값 가져오기
//   System.out.println("-> current_passwd: " + current_passwd);
//   
//   try {
//     Thread.sleep(3000);
//   } catch(Exception e) {
//     
//   }
//   
//   int memberno = (int)session.getAttribute("memberno"); // session에서 가져오기
//   HashMap<String, Object> map = new HashMap<String, Object>();
//   map.put("memberno", memberno);
//   map.put("passwd", current_passwd);
//   
//   int cnt = this.memberProc.passwd_check(map); // 현재 로그인한 사용자의 패스워드 확인
//   
//   JSONObject json = new JSONObject();
//   json.put("cnt", cnt);  // 1: 패스워드 일치, 0: 불일치
//   System.out.println(json.toString());
//   
//   return json.toString();   
//  }
//  
//  /**
//  * 패스워드 변경
//  * @param session
//  * @param model
//  * @param current_passwd 현재 패스워드
//  * @param passwd 새로운 패스워드
//  * @return
//  */
//  @PostMapping(value="/passwd_update_proc")
//  public String passwd_update_proc(HttpSession session, 
//                                   Model model, 
//                                   @RequestParam(value="current_passwd", defaultValue = "") String current_passwd, 
//                                   @RequestParam(value="passwd", defaultValue = "") String passwd) {
//   
//   if (this.memberProc.isMember(session)) {
//     int memberno = (int) session.getAttribute("memberno"); // session에서 가져오기
//     HashMap<String, Object> map = new HashMap<String, Object>();
//     map.put("memberno", memberno);
//     map.put("passwd", current_passwd);
//  
//     int cnt = this.memberProc.passwd_check(map);
//     
//     if (cnt == 0) { // 패스워드 불일치
//       model.addAttribute("code", "passwd_not_equal");
//       model.addAttribute("cnt", 0);
//       
//     } else { // 패스워드 일치
//       map = new HashMap<String, Object>();
//       map.put("memberno", memberno);
//       map.put("passwd", passwd); // 새로운 패스워드
//  
//       int passwd_change_cnt = this.memberProc.passwd_update(map);
//       
//       if (passwd_change_cnt == 1) {
//         model.addAttribute("code", "passwd_change_success");
//         model.addAttribute("cnt", 1);
//       } else {
//         model.addAttribute("code", "passwd_change_fail");
//         model.addAttribute("cnt", 0);
//       }
//     }
//  
//     return "/member/msg";   // /templates/member/msg.html
//   } else {
//     return "redirect:/member/login_cookie_need"; // redirect
//   }
//  
//  }
  //----------------------------------------------------------------------------------
  // 패스워드 수정 및 변경 메서드 컨트롤러 종료
  // ---------------------------------------------------------------------------------
}
