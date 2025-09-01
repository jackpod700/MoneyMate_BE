package $custom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class StockHistoryFetcher {

    private static String apiKey;
    private static Connection connection;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            props.load(input);
        }
        apiKey = props.getProperty("eodhd.api.key");

        String url = props.getProperty("jdbc.batch.url");
        String user = props.getProperty("spring.datasource.username");
        String pass = props.getProperty("spring.datasource.password");

        connection = DriverManager.getConnection(url, user, pass);

        List<Stock> stocks = loadStocks();

        for (Stock stock : stocks) {
            String apiUrl;
            boolean isKor = "KO".equalsIgnoreCase(stock.getExchangeId());

            /**
             * isKor : exchage_id가 KO
             */
            if (isKor) {
                ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
                String endDateTime = nowKST
                        .minusDays(1)
                        .toLocalDate()
                        .atTime(23, 59)
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

                apiUrl = String.format(
                        "https://api.stock.naver.com/chart/domestic/item/%s/day?startDateTime=197001010000&endDateTime=%s",
                        stock.getTicker(),
                        endDateTime
                );
            } else {
                apiUrl = String.format(
                        "https://eodhd.com/api/eod/%s.%s?api_token=%s&fmt=json",
                        stock.getTicker(),
                        stock.getExchangeId(),
                        apiKey
                );
            }

            try {
                String response = new java.util.Scanner(new java.net.URL(apiUrl).openStream(), "UTF-8")
                        .useDelimiter("\\A").next();

                JsonNode root = objectMapper.readTree(response);

                String sql = "INSERT IGNORE INTO stock_price_history " +
                        "(ISIN, date, open_price, high_price, low_price, end_price) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int batchSize = 1000;
                    int count = 0;

                    for (JsonNode node : root) {
                        LocalDate date;
                        BigDecimal open, high, low, close;

                        if (isKor) {
                            date = LocalDate.parse(node.get("localDate").asText(),
                                    java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                            open = node.get("openPrice").decimalValue();
                            high = node.get("highPrice").decimalValue();
                            low  = node.get("lowPrice").decimalValue();
                            close= node.get("closePrice").decimalValue();
                        } else {
                            date = LocalDate.parse(node.get("date").asText());
                            open = node.get("open").decimalValue();
                            high = node.get("high").decimalValue();
                            low  = node.get("low").decimalValue();
                            close= node.get("close").decimalValue();
                        }

                        ps.setString(1, stock.getIsin());
                        ps.setDate(2, Date.valueOf(date));
                        ps.setBigDecimal(3, open);
                        ps.setBigDecimal(4, high);
                        ps.setBigDecimal(5, low);
                        ps.setBigDecimal(6, close);

                        ps.addBatch();
                        count++;

                        if (count % batchSize == 0) {
                            ps.executeBatch();
                            log.info("{}.{} INFO: {} rows 처리완료", stock.getTicker(), stock.getExchangeId(), count);
                        }
                    }

                    ps.executeBatch();
                    log.info("{}.{} [{}] Batch INFO: Total {} saved.",
                            stock.getTicker(), stock.getExchangeId(), stock.getName(), root.size());
                }

            } catch (Exception e) {
                log.error("[FAIL] {}.{} [{}] FAIL: {}",
                        stock.getTicker(), stock.getExchangeId(), stock.getName(), e.getMessage());
            }
        }


        connection.close();
    }

    /**
     * 주식 데이터 조회
     * @return list
     * @throws SQLException
     */
    private static List<Stock> loadStocks() throws SQLException {
        List<Stock> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT isin, ticker, exchange_id, name, currency FROM stock")) {
            while (rs.next()) {
                Stock s = new Stock();
                s.setIsin(rs.getString("isin"));
                s.setTicker(rs.getString("ticker"));
                s.setExchangeId(rs.getString("exchange_id"));
                s.setName(rs.getString("name"));
                s.setCurrency(rs.getString("currency"));
                list.add(s);
            }
        }
        return list;
    }
}
