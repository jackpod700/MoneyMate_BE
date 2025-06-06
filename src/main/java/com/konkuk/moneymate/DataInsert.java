package com.konkuk.moneymate;


import com.konkuk.moneymate.activities.entity.*;
import com.konkuk.moneymate.activities.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Properties;

import java.io.*;
import java.sql.*;
import java.util.Properties;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInsert implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final MonthlyAssetHistoryRepository monthlyAssetHistoryRepository;

    @Override
    public void run(String ... args) throws Exception {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            props.load(input);
            log.info("application.properties 읽기 성공");
        } catch (IOException e) {
            log.error("application.properties 읽기 실패: {}", e.getMessage());
            return;
        }

        String jdbcUrl = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        if (jdbcUrl == null || username == null || password == null) {
            log.error("application.properties 설정을 확인하세요");
            return;
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            log.info("DB connect success");

            String currentDir = System.getProperty("user.dir");
            log.info("current dir: {}", currentDir);

            executeSqlScript(conn, "src/main/resources/sql/insert_users.sql");
            executeSqlScript(conn, "src/main/resources/sql/insert_bankaccount.sql");
            executeSqlScript(conn, "src/main/resources/sql/insert_transaction.sql");

            log.info("All SQL execution success");
        } catch (SQLException e) {
            log.error("DB connection failed: {}", e.getMessage());
        }

        User user1 = new User("user_id", "user", "$2a$12$ZNsd2Al0w2hjPeq9icxl.Oydgv3.hbQ3UlsWCqkJfmXeD8t6Wq9sm", "010-0000-0000", LocalDate.of(2000, 1, 1));
        User user2 = new User("admin_id", "admin", "$2a$12$T05/pakINgU7nUagCdInRe8rC6xPK1sHuhxlUuSIQPENfAogqeGpG", "010-1111-2222", LocalDate.of(2001, 12, 31));

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

    private static void executeSqlScript(Connection conn, String filePath) {
        log.info("실행 중: {}", filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             Statement stmt = conn.createStatement()) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // 주석 -- 제거
                line = line.trim();
                if (line.startsWith("--") || line.isEmpty()) continue;
                sqlBuilder.append(line).append(" ");
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().replaceAll(";$", "").trim();
                    stmt.executeUpdate(sql);
                    sqlBuilder.setLength(0);
                }
            }
            log.info("SQL File {} execution completed", filePath);
        } catch (IOException e) {
            log.error("SQL file read failed: {} ", e.getMessage());
        } catch (SQLException e) {
            log.error("SQL execution failed: {}",e.getMessage());
        }
    }
}

