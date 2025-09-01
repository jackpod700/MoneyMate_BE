package $custom;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

@Slf4j
public class DataInsert {

    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            props.load(input);
            log.info("application.properties 읽기 성공 ");
        } catch (IOException e) {
            System.err.println("application.properties 읽기 실패: " + e.getMessage());
            return;
        }

        String jdbcUrl  = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        if (jdbcUrl == null || username == null || password == null) {
            System.err.println("jdbc.url / jdbc.username / jdbc.password 확인!!");
            return;
        }

        String driverClass = props.getProperty("jdbc.driver");
        if (driverClass != null && !driverClass.isBlank()) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                System.err.println("JDBC Driver load FAILED: " + e.getMessage());
                return;
            }
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            log.info("DB connection success");
            log.info("current dir: {}", System.getProperty("user.dir"));

            String[] scripts = {
                    "insert_users",
                    "insert_stock",
                    "insert_bankaccount",
                    "insert_stockaccount",
                    "insert_asset",
                    "insert_transaction",
                    "stock_transaction_dummy",
                    "stock_history_dummy",
                    "exchange_history_dummy"
            };

            for (String name : scripts) {
                String resourcePath = "sql/" + (name.endsWith(".sql") ? name : name + ".sql");
                executeSqlScriptFromClasspath(conn, resourcePath);
            }

            log.info("ALL SQL executed");
        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * 클래스패스(resources)에서 SQL 파일을 읽어 세미콜론(;) 단위로 실행
     * - '--' 라인 주석 제거
     * - 빈 라인 무시
     * - 한 줄 안의 여러 구문도 처리(누적 후 ';' 기준 split)
     */
    private static void executeSqlScriptFromClasspath(Connection conn, String resourcePath) {
        log.info("실행 중: {}", resourcePath);

        try (InputStream is = DataInsert.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Resources CANNOT found: " + resourcePath);
                return;
            }

            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    int idx = trimmed.indexOf("--");
                    if (idx >= 0) trimmed = trimmed.substring(0, idx).trim();

                    sb.append(trimmed).append(' ');

                    String buffer = sb.toString();
                    if (buffer.contains(";")) {
                        String[] parts = buffer.split(";");
                        for (int i = 0; i < parts.length - 1; i++) {
                            String sql = parts[i].trim();
                            if (!sql.isEmpty()) {
                                stmt.executeUpdate(sql);
                            }
                        }
                        sb.setLength(0);
                        String last = parts[parts.length - 1].trim();
                        if (!last.isEmpty()) {
                            sb.append(last).append(' ');
                        }
                    }
                }

                String rest = sb.toString().trim();
                if (!rest.isEmpty()) {
                    stmt.executeUpdate(rest);
                }

                log.info("SQL file executed: {}", resourcePath);
            }
        } catch (IOException e) {
            System.err.println("SQL file load FAILED(" + resourcePath + "): " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL execution FAILED(" + resourcePath + "): " + e.getMessage());
        }
    }
}
