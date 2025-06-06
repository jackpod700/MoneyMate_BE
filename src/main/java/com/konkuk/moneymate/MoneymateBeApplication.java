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

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class MoneymateBeApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final AssetRepository assetRepository;
	private final BankAccountRepository bankAccountRepository;
	private final TransactionRepository transactionRepository;
	private final MonthlyAssetHistoryRepository monthlyAssetHistoryRepository;

	public static void main(String[] args) {
		SpringApplication.run(MoneymateBeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user1 = new User("user_id1", "user", "$2a$12$ZNsd2Al0w2hjPeq9icxl.Oydgv3.hbQ3UlsWCqkJfmXeD8t6Wq9sm", "010-0000-0000", LocalDate.of(2000, 1, 1));
		User user2 = new User("admin_id1", "admin", "$2a$12$T05/pakINgU7nUagCdInRe8rC6xPK1sHuhxlUuSIQPENfAogqeGpG", "010-1111-2222", LocalDate.of(2001, 12, 31));

		BankAccount bankAccount1 = new BankAccount(user1, "국민은행", "123-4567890-1-234", user1.getUserName(), 100000L, "예적금");
		BankAccount bankAccount2 = new BankAccount(user2, "카카오뱅크", "3333-11-1234567", user2.getUserName(), 50000L, "예적금");

		Transaction transaction1 = new Transaction(bankAccount2, bankAccount1.getAccountNumber(), null, 20000, "이체", LocalDateTime.of(2025, 5, 24, 10, 30), 120000L);
		Transaction transaction2 = new Transaction(bankAccount1, bankAccount2.getAccountNumber(), 20000, null, "이체", LocalDateTime.of(2025, 5, 24, 10, 32), 100000L);
		Transaction transaction3 = new Transaction(bankAccount1, bankAccount2.getAccountNumber(), 30000, null, "이체", LocalDateTime.of(2025, 5, 24, 10, 33), 50000L);

		Asset asset1 = new Asset(user1, 3700000000L, "반포자이 84A", "real-estate");
		Asset asset2 = new Asset(user1, 50000000L, "제네시스 G80/2G", "car");
		Asset asset3 = new Asset(user2, 2147483637L, "e편한세상광진그랜드파크 105A", "real-estate");

		MonthlyAssetHistory monthlyAssetHistory1 = new MonthlyAssetHistory(user1, LocalDate.of(2025, 5, 1), 100000L, 0L, 50000L);
		MonthlyAssetHistory monthlyAssetHistory2 = new MonthlyAssetHistory(user2, LocalDate.of(2025, 5, 1), 50000L, 30000L, 0L);

		userRepository.save(user1);
		userRepository.save(user2);
		bankAccountRepository.save(bankAccount1);
		bankAccountRepository.save(bankAccount2);
		transactionRepository.save(transaction1);
		transactionRepository.save(transaction2);
		transactionRepository.save(transaction3);
		assetRepository.save(asset1);
		assetRepository.save(asset2);
		assetRepository.save(asset3);
		monthlyAssetHistoryRepository.save(monthlyAssetHistory1);
		monthlyAssetHistoryRepository.save(monthlyAssetHistory2);
	}
}
