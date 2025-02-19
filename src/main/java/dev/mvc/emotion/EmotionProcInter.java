package dev.mvc.emotion;

import java.util.ArrayList;
import java.util.HashMap;

import dev.mvc.board.BoardVO;

public interface EmotionProcInter {
  
  /**
   * 감정 생성
   * @param emotio1nVO
   * @return
   */
  public int create(EmotionVO emotionVO);
  
  /**
   * 감정 조회
   * @param emno
   * @return
   */
  public EmotionVO read(int emono);
  
  /**
   * 모든 목록
   * @return
   */
  public ArrayList<EmotionVO> list_all();

  
  public ArrayList<EmotionVO> image_list();
  
  /**
   * 게시글 등록된 목록
   * @param emono
   * @return
   */
  public ArrayList<EmotionVO> list_by_emono(int emono);
  
  /**
   * 게시글 내용 수정
   * @param emotionVO
   * @return
   */
  public int update_text(EmotionVO emotionVO);
  
  /**
   * 파일 수정
   * @param emotionVO
   * @return
   */
  public int update_file(EmotionVO emotionVO);
  
  /**
   * 게시글 삭제
   * @param emono
   * @return
   */
  public int delete(int emono);
  
  /**
   * 게시글 종류별 검색 목록
   * @param hashMap
   * @return
   */
  public ArrayList<EmotionVO> list_by_emono_search(HashMap<String, Object> hashMap);
  
  /**
   * 게시글 종류별 검색 레코드 갯수
   * @param hashMap
   * @return
   */
  public int count_by_emono_search(HashMap<String, Object> hashMap);
  
  /**
   * 게시글 종류별 검색 및 페이징
   * @param map
   * @return
   */
  public ArrayList<EmotionVO> list_by_emono_search_paging(HashMap<String, Object> map);
  
  /** 
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작 
   * 현재 페이지: 11 / 22   [이전] 11 12 13 14 15 16 17 18 19 20 [다음] 
   *
   * @param cateno 카테고리 번호
   * @param now_page 현재 페이지
   * @param word 검색어
   * @param list_file 목록 파일명
   * @param search_count 검색 레코드수   
   * @param record_per_page 페이지당 레코드 수
   * @param page_per_block 블럭당 페이지 수
   * @return 페이징 생성 문자열
   */
  public String pagingBox(int memberno, int now_page, String word, String list_file, int search_count, 
      int record_per_page, int page_per_block);
}
