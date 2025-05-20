package com.konkuk.moneymate;

import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class MoneymateBeApplication implements CommandLineRunner {

	private final UserRepository userRepository;

	public MoneymateBeApplication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(MoneymateBeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userRepository.save(new User("user_id","user", "$2a$12$j8b7ZPi/44Otq2khAgfAQOeqDH.Y3sUIWIGNrk9Tf2j6jj2zU8udO"));
		userRepository.save(new User("admin_id", "admin", "$2a$12$T05/pakINgU7nUagCdInRe8rC6xPK1sHuhxlUuSIQPENfAogqeGpG"));
	}

}
