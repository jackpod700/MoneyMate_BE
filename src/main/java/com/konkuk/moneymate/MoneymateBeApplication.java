package com.konkuk.moneymate;

import com.konkuk.moneymate.activities.entity.*;
import com.konkuk.moneymate.activities.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <h3>MoneymateBeApplication : 메인 실행 클래스 </h3>
 */
@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class MoneymateBeApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MoneymateBeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/**
		 * DataInsert: Spring bean 으로 이미 들어가 있습니다
		 */
		log.info("Moneymate-BE Application started");
	}
}
