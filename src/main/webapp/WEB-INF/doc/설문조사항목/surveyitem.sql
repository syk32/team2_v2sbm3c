/**********************************/
/* Table Name: 설문 조사 항목 */
/**********************************/

-- 테이블 삭제
DROP TABLE surveyitem;
DROP TABLE surveyitem CASCADE CONSTRAINTS;

CREATE TABLE surveyitem (
    itemno      NUMBER(10)      NOT NULL    PRIMARY KEY,
    surveyno    NUMBER(10)      NOT NULL,
--    memberno    NUMBER(10)      NOT NULL,
    item_seq    NUMBER(5)       NOT NULL,
    item        VARCHAR2(200)   NOT NULL,
    item_cnt    NUMBER(7)       DEFAULT 0    NULL,
    FOREIGN KEY (surveyno)  REFERENCES survey (surveyno) ON DELETE CASCADE
--    FOREIGN KEY (memberno)  REFERENCES member (memberno)
);
DESC SURVEYITEM;

COMMENT ON TABLE SURVEYITEM is '설문 조사 항목';
COMMENT ON COLUMN SURVEYITEM.ITEMNO is '설문 조사 항목 번호';
COMMENT ON COLUMN SURVEYITEM.SURVEYNO is '설문 조사 번호';
--COMMENT ON COLUMN SURVEYITEM.MEMBERNO is '회원 번호';
COMMENT ON COLUMN SURVEYITEM.ITEM_SEQ is '항목 출력 순서';
COMMENT ON COLUMN SURVEYITEM.ITEM is '항목';
COMMENT ON COLUMN SURVEYITEM.ITEM_CNT is '항목 선택 인원';

DROP SEQUENCE surveyitem_seq;

CREATE SEQUENCE surveyitem_seq
  START WITH 1              -- 시작 번호
  INCREMENT BY 1            -- 증가값
  MAXVALUE 9999999999       -- 최대값: 9999999999 --> NUMBER(10) 대응
  CACHE 2                   -- 2번은 메모리에서만 계산
  NOCYCLE;  

COMMIT;

-- 등록
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 7, 1, '주 5회 이상');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 7, 2, '주 3회 이상');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 7, 3, '자주 사용 안함');

INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 9, 1, '예');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 9, 2, '아니요');

INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 13, 1, '쉬웠어요');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 13, 2, '어려웠어요');

INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 10, 1, '그림 생성');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 10, 2, '일정 조회');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 10, 2, '일기 작성');

INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 5, 1, '예');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 5, 2, '아니요');

INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 14, 1, '있었어요');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 14, 2, '없었어요');

INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 1, 4, '테스트항목');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 15, 1, '즐거워해요');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 15, 2, '보통');
INSERT INTO surveyitem(itemno, surveyno, item_seq, item)
VALUES (surveyitem_seq.nextval, 15, 3, '즐거워하지 않아요');

-- 조회
SELECT * FROM surveyitem;

SELECT * FROM surveyitem WHERE surveyno = 1;
    ITEMNO   SURVEYNO   ITEM_SEQ ITEM                                                                                                                                                                                                       ITEM_CNT
---------- ---------- ---------- -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- ----------
         1         31          1 이 서비스를 일주일에 5회 이상 사용한다.                                                                                                                                                                          10

-- 수정
UPDATE surveyitem SET item='이 서비스를 일주일에 2회 이하 사용한다.' WHERE itemno=1;
    ITEMNO   SURVEYNO   ITEM_SEQ ITEM                                                                                                                                                                                                       ITEM_CNT
---------- ---------- ---------- -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- ----------
         1         31          1 이 서비스를 일주일에 2회 이하 사용한다.                                                                                                                                                                          10

UPDATE surveyitem SET item_seq='5' WHERE itemno=9;
-- 삭제
DELETE FROM surveyitem;

DELETE FROM surveyitem WHERE surveyno = 1;

SELECT surveyno, SUM(item_cnt) AS total_participants
FROM surveyitem
WHERE surveyno = 1
GROUP BY surveyno;

SELECT *
FROM surveyitem
WHERE surveyno = 1
ORDER BY item_seq ASC;

SELECT COUNT(*) as cnt 
FROM surveyitem 
WHERE surveyno=1;

UPDATE surveyitem
SET item_cnt = item_cnt + 1
WHERE itemno = 5;
    
UPDATE surveyitem
SET item_cnt = 0
WHERE itemno = 17;

SELECT itemno, surveyno, item_seq, item, item_cnt
    FROM surveyitem
    WHERE itemno = 1;

COMMIT;
SELECT * FROM surveyitem;
-- 검색
SELECT itemno, surveyno, item_seq, item
FROM surveyitem
WHERE (UPPER(item) LIKE '%' || UPPER('서비스') || '%')
ORDER BY itemno ASC;

-- 검색 갯수
SELECT COUNT(*) as cnt
FROM surveyitem
WHERE (UPPER(item) LIKE '%' || UPPER('서비스') || '%');

-- ③ 정렬 -> ROWNUM -> 분할
SELECT itemno, surveyno, item_seq, item, r
FROM (
    SELECT itemno, surveyno, item_seq, item, rownum as r
    FROM (
        SELECT itemno, surveyno, item_seq, item
        FROM surveyitem
        WHERE (UPPER(item) LIKE '%' || UPPER('서비스') || '%')
        ORDER BY itemno ASC
    )
)
WHERE r >= 1 AND r <= 2;

SELECT COUNT(*) 
    FROM participants 
    WHERE itemno = 24 AND memberno = 2;
  COUNT(*)
----------
         2

SELECT COUNT(*) 
FROM participants 
WHERE itemno = 2;

-- JOIN
SELECT survey.surveyno, survey.topic,
        surveyitem.itemno, surveyitem.item, surveyitem.item_seq, surveyitem.item_cnt
FROM survey s, surveyitem i
ORDER BY itemno ASC;

SELECT s.surveyno, s.topic,
        i.itemno, i.item, i.item_seq, i.item_cnt
FROM survey s, surveyitem i
WHERE s.surveyno = i.surveyno
ORDER BY itemno ASC;

SELECT s.surveyno, s.topic as s.surveyno
        i.surveyno, i.itemno, i.item, i.item_seq, i.item_cnt as i.surveyno
FROM survey s, surveyitem i
WHERE (s.surveyno = i.surveyno) AND s.topic='사용 빈도'
ORDER BY itemno ASC;
        
SELECT 
    s.surveyno AS survey_number, 
    s.topic AS topic_name,
    i.surveyno AS item_survey_number, 
    i.itemno AS item_number, 
    i.item AS item_name, 
    i.item_seq AS item_sequence, 
    i.item_cnt AS item_count
FROM 
    survey s
JOIN 
    surveyitem i
ON 
    s.surveyno = i.surveyno
WHERE 
    s.topic = '사용 빈도'
ORDER BY 
    i.itemno ASC;

DELETE FROM surveyitem
WHERE surveyno=14;

-- JOIN, 어느 공지사항을 누가 추천 했는가?
SELECT itemno, surveyno, item_seq, item, item_cnt
FROM noticegood
ORDER BY noticegoodno DESC;

-- 테이블 2개 join
SELECT i.itemno, i.surveyno, i.item_seq, i.item, i.item_cnt, s.topic
FROM survey s, surveyitem i
WHERE s.surveyno = i.surveyno
ORDER BY itemno DESC;

-- 테이블 3개 join, as 사용시 컬럼명 변경 가능: c.title as n_title
SELECT i.itemno, i.surveyno, i.item_seq, i.item, i.item_cnt, s.topic as s_topic, m.memberno, m.id, m.name
FROM notice c, noticegood r, member m
WHERE c.noticeno = r.noticeno AND r.memberno = m.memberno
ORDER BY noticegoodno DESC;
        
