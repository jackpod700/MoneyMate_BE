package com.konkuk.moneymate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MoneymateBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneymateBeApplication.class, args);
	}

}
