package com.konkuk.moneymate;


import java.util.TimeZone;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <h3>MoneymateBeApplication : 메인 실행 클래스 </h3>
 */
@Slf4j
@AllArgsConstructor
@EnableScheduling
@SpringBootApplication
public class MoneymateBeApplication implements CommandLineRunner {
    // 8월21일에 추가한 주석입니다
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(MoneymateBeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Moneymate-BE Application started");
    }
}