DROP TABLE diary;
CREATE TABLE diary (
    diaryno      NUMBER(30) PRIMARY KEY,
    title        VARCHAR2(50) NULL,
    ddate        DATE               NULL,
    summary      CLOB,
    weatherno    NUMBER(10) NULL,
    emono         NUMBER(10)       NULL,
    memberno     NUMBER(10)      NULL,
    cnt          NUMBER(7)  NULL, 
    goodcnt          NUMBER(7)  NULL,
    FOREIGN KEY (emono) REFERENCES EMOTION(emono),
    FOREIGN KEY (memberno) REFERENCES MEMBER(membernno),
    FOREIGN KEY (weatherno) REFERENCES WEATHER(weatherno)
);

commit;

DROP SEQUENCE diary_seq;

CREATE SEQUENCE diary_seq
  START WITH 1              -- 시작 번호
  INCREMENT BY 1          -- 증가값
  MAXVALUE 9999999999 -- 최대값: 9999999 --> NUMBER(7) 대응
  CACHE 2                       -- 2번은 메모리에서만 계산
  NOCYCLE;                     -- 다시 1부터 생성되는 것을 방지
  
  
SELECT table_name FROM user_tables WHERE table_name = 'DIARY';

SELECT COUNT(*) 
FROM diary 
WHERE 1=1
  AND (title LIKE '%' || :title || '%' OR :title IS NULL OR :title = '')
  AND (TRUNC(ddate) >= TO_DATE(:start_date, 'YYYY-MM-DD') OR :start_date IS NULL OR :start_date = '')
  AND (TRUNC(ddate) <= TO_DATE(:end_date, 'YYYY-MM-DD') OR :end_date IS NULL OR :end_date = '');

SELECT COUNT(*) as cnt;

ALTER TABLE diary
ADD CONSTRAINT fk_diary_emotion
FOREIGN KEY (emono) REFERENCES EMOTION(emono);

-- MEMBER 테이블과의 외래 키 추가
ALTER TABLE diary
ADD CONSTRAINT fk_diary_member
FOREIGN KEY (memberno) REFERENCES MEMBER(memberno);

-- WEATHER 테이블과의 외래 키 추가
ALTER TABLE diary
ADD CONSTRAINT fk_diary_weather
FOREIGN KEY (weatherno) REFERENCES WEATHER(weatherno);

