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

@Slf4j
public class DataInsert {

    public static void main(String[] args) {
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

