
# README.MD (작성중)
<strong>push마다 반영할 예정입니다</strong>

## Package 구성

<strong>Packages 구성중</strong>

## Database
MySQL 사용<br>
- 접속 주소: Application.properties 파일 확인

## Test
test directory 안의 패키지 구조는 main\java와 똑같이 해주세요

<br><br>

## COMMIT

<li>05-22-2 jwt payload 정보에 uid 추가하도록 설정 </li>
요청 보낼때 jwt 앞에 Bearer 붙여주세요 <br>
테스트 클래스 검증 완료 <br><br>


<li>05-24-1 Entity 클래스 작성1 </li>
transaction의 in, out을 income, outcome으로 수정했습니다 (예약어 대체) <br>
asset의 price를 long으로 변경했습니다<br><br>

<li>05-24-2 Entity 클래스 작성2, 테스트 데이터 작성 </li>
Application 클래스에 테스트 데이터를 삽입했습니다<br>
나중에 dummy data들을 따로 저장하는 클래스로 분리하려고 합니다






<br><br><br><br><br><br>
- Application.properties 

spring.application.name=moneymate-BE

spring.datasource.url=jdbc:mysql://moneymate-db.c7we2em0qc98.ap-northeast-2.rds.amazonaws.com:3306/moneymate3
spring.datasource.username=admin
spring.datasource.password=moneymate
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

jdbc.url=jdbc:jdbc:mysql://moneymate-db.c7we2em0qc98.ap-northeast-2.rds.amazonaws.com:3306/moneymate3

spring.jpa.hibernate.ddl-auto= create-drop
spring.jpa.show-sql=true

spring.data.rest.base-path=/api
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true










<style>

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
