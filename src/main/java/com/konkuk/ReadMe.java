package com.konkuk;

public class ReadMe {
}


/**
 *
 * README.md, build.gradle이 git ignore 이기 때문에 내용을 여기다 추가합니다.
 *
 */

/**
 * Login api 테스트 방법
 * user 테이블 보고 body에 userid, password json으로 포함해서
 * POST /login 전송하면 200 OK와 함꼐 헤더에 token을 받습니다
 *
 * {
 *     "userid":"user_id",
 *     "password":"user"
 * }
 *
 * 현재 모든 예외처리가 401로 되어있습니다
 *
 * 이후 로그인해서 활동하는 api는 http 헤더에 Authorized, token 값 붙여넣어서 테스트해주세요
 */

/**
 * applicaiton.properties
 *
 *
 spring.datasource.url=jdbc:mysql://moneymate-db.c7we2em0qc98.ap-northeast-2.rds.amazonaws.com:3306/moneymate2
 spring.datasource.username=admin
 spring.datasource.password=moneymate
 spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

 * jdbc.url=jdbc:jdbc:mysql://moneymate-db.c7we2em0qc98.ap-northeast-2.rds.amazonaws.com:3306/moneymate3
 * jdbc.username=admin
 * jdbc.password=moneymate
 *
 *
 *
 * create-drop : H2처럼 서버 실행시에만 유지
 * 용도에 맞게 속성 변경해주세요
 *
 * spring.jpa.hibernate.ddl-auto= create-drop
 * spring.jpa.show-sql=true
 */

/**
 * build.gradle
 *
 * JWT dependencies 추가:
 * implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
 * runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5', 'io.jsonwebtoken:jjwt-jackson:0.11.5'
 *
 */

/**
 * SWAGGER 사용하기
 *  implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0'
 */


/**
 * CODEF 라이브러리
 * implementation("io.codef.api:easycodef-java-v2:2.0.0-beta-005")
 *
 */