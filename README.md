
# README.MD 
<strong>push마다 반영할 예정입니다</strong>

## Package 구성

<strong>Packages 구성중</strong>

## Database
MySQL / Redis 사용
- Application.properties 확인

## Test
test directory 안의 패키지 구조는 main\java와 똑같이 해주세요

<br><br>

## COMMIT

...

<hr>
<li>08-18-3 소비통계 api 작성</li>

<hr>
<li>08-13-2 데이터 추가예정</li>

<hr>
<li>08-06-1 데이터베이스 이전</li>
접속 주소는 application.properties 참고해 주세요 <br>
접속 계정과 패스워드는 동일합니다 <br><br>
DataInsert 클래스 수정 <br>
jpa ddl 옵션을 create 에서 update로 변경해서 개발 테스트로 실행 중에 중복 tuple 오류 발생할 수 있습니다
<br>두번째 실행부터는 @Component 주석해제해서 DataInsert를 Spring bean으로 지정하지 않게 해주세요
<hr>

<li>05-22-2 jwt payload 정보에 uid 추가하도록 설정 </li>
요청 보낼때 jwt 앞에 Bearer 붙여주세요 <br>
테스트 클래스 검증 완료
<hr>
<li>05-24-1 Entity 클래스 작성1 </li>
transaction의 in, out을 income, outcome으로 수정했습니다 (예약어 대체) <br>
asset의 price를 long으로 변경했습니다
<hr>
<li>05-24-2 Entity 클래스 작성2, 테스트 데이터 작성 </li>
Application 클래스에 테스트 데이터를 삽입했습니다<br>
나중에 dummy data들을 따로 저장하는 클래스로 분리하려고 합니다 
<hr>







<br><br><br><br><br><br><br>

<style>

@font-face {
    font-family: 'MaruBuri';
    src: url(https://hangeul.pstatic.net/hangeul_static/webfont/MaruBuri/MaruBuri-Regular.eot);
    src: url(https://hangeul.pstatic.net/hangeul_static/webfont/MaruBuri/MaruBuri-Regular.eot?#iefix) format("embedded-opentype"), url(https://hangeul.pstatic.net/hangeul_static/webfont/MaruBuri/MaruBuri-Regular.woff2) format("woff2"), url(https://hangeul.pstatic.net/hangeul_static/webfont/MaruBuri/MaruBuri-Regular.woff) format("woff"), url(https://hangeul.pstatic.net/hangeul_static/webfont/MaruBuri/MaruBuri-Regular.ttf) format("truetype");
}

* {
    font-family: MaruBuri;
}

h1 {
    color: gray;
}

h2 {
    color: greenyellow;
    font-size: 24px;
}

h3 {
    color: deepskyblue;
    font-size: 20px;
}

p {
    font-size: 16px;
    padding: 0;
}

strong {
    font-size: 16px;
    color: aquamarine;
}

li {
    font-size:16px;
    font-weight: bold;
    color: lemonchiffon;
}

</style>
