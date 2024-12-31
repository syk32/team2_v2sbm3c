package dev.mvc.illustration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mvc.diary.DiaryVO;

@Component("dev.mvc.illustration.IllustrationProc")
public class IllustrationProc implements IllustrationProcInter {

    @Autowired
    private IllustrationDAOInter illustrationDAO;

    @Override
    public int create(IllustrationVO illustrationVO) {
        return illustrationDAO.create(illustrationVO);
    }

    @Override
    public ArrayList<IllustrationVO> list_all() {
        return illustrationDAO.list_all();
    }

    @Override
    public ArrayList<IllustrationVO> list_by_illustno(int illustno) {
        return illustrationDAO.list_by_illustno(illustno);
    }

    @Override
    public IllustrationVO read(int illustno) {
        return illustrationDAO.read(illustno);
    }

    @Override
    public int update_text(IllustrationVO illustrationVO) {
        return illustrationDAO.update_text(illustrationVO);
    }

    @Override
    public int update_file(IllustrationVO illustrationVO) {
        return illustrationDAO.update_file(illustrationVO);
    }

    @Override
    public int delete(int illustno) {
        return illustrationDAO.delete(illustno);
    }

    @Override
    public int list_by_illustno_search_count(HashMap<String, Object> hashMap) {
        return illustrationDAO.list_by_illustno_search_count(hashMap);
    }

    @Override
    public ArrayList<IllustrationVO> list_by_illustno_search_paging(HashMap<String, Object> map) {
        return illustrationDAO.list_by_illustno_search_paging(map);
    }

    @Override
    public int count_by_illustno(int illustno) {
        return illustrationDAO.count_by_illustno(illustno);
    }

    @Override
    public String pagingBox(int now_page, String list_file_name, int search_count, int record_per_page, int page_per_block) {
        // 전체 페이지 수 계산
        int total_page = (int) Math.ceil((double) search_count / record_per_page);
        // 전체 그룹 수 계산
        int total_grp = (int) Math.ceil((double) total_page / page_per_block);
        // 현재 그룹 계산
        int now_grp = (int) Math.ceil((double) now_page / page_per_block);

        // 현재 그룹의 시작 페이지와 끝 페이지
        int start_page = ((now_grp - 1) * page_per_block) + 1;
        int end_page = now_grp * page_per_block;

        // 마지막 페이지를 전체 페이지로 제한
        end_page = Math.min(end_page, total_page);

        StringBuilder pagingHtml = new StringBuilder();
        pagingHtml.append("<style type='text/css'>");
        pagingHtml.append("  #paging {text-align: center; margin-top: 5px; font-size: 1em;}");
        pagingHtml.append("  #paging A:link {text-decoration:none; color:black; font-size: 1em;}");
        pagingHtml.append("  #paging A:hover{text-decoration:none; background-color: #FFFFFF; color:black; font-size: 1em;}");
        pagingHtml.append("  #paging A:visited {text-decoration:none;color:black; font-size: 1em;}");
        pagingHtml.append("  .span_box_1{");
        pagingHtml.append("    text-align: center;");
        pagingHtml.append("    font-size: 1em;");
        pagingHtml.append("    border: 1px;");
        pagingHtml.append("    border-style: solid;");
        pagingHtml.append("    border-color: #cccccc;");
        pagingHtml.append("    padding:1px 6px 1px 6px;");
        pagingHtml.append("    margin:1px 2px 1px 2px;");
        pagingHtml.append("  }");
        pagingHtml.append("  .span_box_2{");
        pagingHtml.append("    text-align: center;");
        pagingHtml.append("    background-color: #668db4;");
        pagingHtml.append("    color: #FFFFFF;");
        pagingHtml.append("    font-size: 1em;");
        pagingHtml.append("    border: 1px;");
        pagingHtml.append("    border-style: solid;");
        pagingHtml.append("    border-color: #cccccc;");
        pagingHtml.append("    padding:1px 6px 1px 6px;");
        pagingHtml.append("    margin:1px 2px 1px 2px;");
        pagingHtml.append("  }");
        pagingHtml.append("</style>");
        pagingHtml.append("<div id='paging'>");

        // 이전 그룹으로 이동
        if (now_grp > 1) {
            int prev_page = (now_grp - 1) * page_per_block;
            pagingHtml.append("<span class='span_box_1'><a href='")
                      .append(list_file_name)
                      .append("?now_page=").append(prev_page)
                      .append("'>이전</a></span>");
        }

        // 페이지 번호 출력
        for (int i = start_page; i <= end_page; i++) {
            if (i == now_page) { // 현재 페이지 강조
                pagingHtml.append("<span class='span_box_2'>").append(i).append("</span>");
            } else { // 다른 페이지는 링크 출력
                pagingHtml.append("<span class='span_box_1'><a href='")
                          .append(list_file_name)
                          .append("?now_page=").append(i)
                          .append("'>").append(i).append("</a></span>");
            }
        }

        // 다음 그룹으로 이동
        if (now_grp < total_grp) {
            int next_page = now_grp * page_per_block + 1;
            pagingHtml.append("<span class='span_box_1'><a href='")
                      .append(list_file_name)
                      .append("?now_page=").append(next_page)
                      .append("'>다음</a></span>");
        }

        pagingHtml.append("</div>");
        return pagingHtml.toString();
    }


    @Override
    public Date getDiaryDateByIllustNo(int illustno) {
        return illustrationDAO.getDiaryDateByIllustNo(illustno);
    }

    @Override
    public int searchCount(int illustno, String word) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("illustno", illustno);
        map.put("word", word);
        return illustrationDAO.list_by_illustno_search_count(map);
    }

    @Override
    public String pagingBox(int illustno, int searchCount, int nowPage, String word) {
        int recordPerPage = 10; // 페이지당 레코드 수
        int pagePerBlock = 5; // 블록당 페이지 수
        String listFileName = "/illustration/list_by_illustno_search_paging_grid";

        return pagingBox(nowPage, listFileName, searchCount, recordPerPage, pagePerBlock);
    }
    
    @Override
    public ArrayList<IllustrationVO> listByIllustNoSearchPaging(int illustno, String word, int nowPage) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("illustno", illustno);
        paramMap.put("word", word);
        paramMap.put("now_page", nowPage);
        return illustrationDAO.listByIllustNoSearchPaging(paramMap);
    }

    @Override
    public List<DiaryVO> listByDateRange(String start_date, String end_date) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("start_date", start_date);
        paramMap.put("end_date", end_date);
        return illustrationDAO.listByDateRange(paramMap);
    }
    
}