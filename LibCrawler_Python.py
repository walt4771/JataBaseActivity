from bs4 import BeautifulSoup
from urllib.request import urlopen
from html_table_parser import parser_functions as parser  # HTML 파서
import pandas as pd  # 데이터프레임
import sys; import os  # 프로그램 종료
import schedule; import time  # 작업 스케줄러
import smtplib; from email.mime.text import MIMEText  # SMTP 모듈과 제목 본문 설정 라이브러리

def HTMLParse():
    # HTML 파싱
    url = "http://222.233.168.6:8094/RoomStatus.aspx"  # 도서관 자리 홈페이지
    result = urlopen(url)
    html = result.read()
    soup = BeautifulSoup(html, 'html.parser')  # BS를 이용해 HTML파싱
    temp = soup.find_all('table')

    # 데이터프레임 생성
    p = parser.make2d(temp[1])  # 데이터 프레임 생성
    df = pd.DataFrame(p[1:],
                      columns=['a', 'b', 'Use', 'Avail', 'Rate', 'Wait', 'f', 'g'],
                      index=['Man', 'Women', 'Adult', 'Adult2', 'Free', 'Notebook', 'Sum'])  # 데이터프레임의 행렬 정의
    del df['a']; del df['b']  # 쓸모없는 부분 삭제
    # print("parse complete")
    return df


def exit():  # 프로그램 종료 함수
    print("\n프로그램 종료"); os.system('pause'); sys.exit(0);


def first(df):
    # 도서관 열었는지 닫았는지 확인
    if df.iat[6, 2] == "0%":  # 전체 이용률 비교
        print("\n도서관이 휴관 또는 닫혀 있습니다")
        exit()
    else:
        print(df)

        if df.iat[5, 3] == "0":  # df.iat[행, 렬]
            print("\n노트북실 대기자가 없습니다.")
            exit()
        else:
            notebooknum = input("노트북실 대기 번호를 입력하세요 : ")
            print("차례가 오면 알려드리겠습니다")
    return notebooknum


def mail():
    s = smtplib.SMTP('smtp.gmail.com', 587)  # 세션 생성
    s.starttls()  # TLS 모드 설정
    # s.login('이메일', '애플리케이션 비밀번호')  # IMAP 설정
    s.login('imap_addr', 'imap_pw')

    msg = MIMEText('내용 : 노트북실 차례가 되었습니다.')  # 제목 설정
    s.sendmail("보내는 주소", "받는 주소", msg.as_string())  # 메일 보내기
    s.quit()  # 세션 종료


def second():  # 루프
    tmpdf = HTMLParse()  # return df
    if temp == tmpdf.iat[5, 4]:  # 호출번호
        print("차례가 되었습니다. "); mail(); print("메일 전송 완료")  # 메일 전송
        exit()

# 첫 실행
temp = first(HTMLParse())  # return notebooknum
# 반복
schedule.every(10).seconds.do(second)  # 작업 스케줄러


while True:  # 실제 루프
    schedule.run_pending()
    time.sleep(1)
