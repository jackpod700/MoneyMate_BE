package com.konkuk.moneymate.activities.news.service;

import static java.lang.Thread.sleep;

import com.google.genai.Client;
import com.google.genai.Client.Builder;
import com.google.genai.types.GenerateContentResponse;
import com.konkuk.moneymate.activities.news.entity.NewsSummarize;
import com.konkuk.moneymate.activities.news.enums.NewsCategoryCode;
import com.konkuk.moneymate.activities.news.repository.NewsSummarizeRepository;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NewsSummarizer {

    private final NewsCollector newsCollector;
    private final NewsSummarizeRepository newsSummarizeRepository;

    public NewsSummarizer(NewsCollector newsCollector, NewsSummarizeRepository newsSummarizeRepository) {
        this.newsCollector = newsCollector;
        this.newsSummarizeRepository = newsSummarizeRepository;
    }

    /**
     * 매일 새벽 4시에 cron 표현식에 따라 자동으로 실행됩니다.
     * cron = "초 분 시 일 월 요일"
     * "0 0 4 * * *" : 매일 4시 0분 0초에 실행
     */
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    private void storeSummary(){
        try {
            HashMap<String, String> summaries = generateNewsSummary(); // category, summary
            List<String> keys = summaries.keySet().stream().toList();
            for (String key : keys) {
                newsSummarizeRepository.save(new NewsSummarize(
                        summaries.get(key),
                        key));
            }
        } catch (Exception e) {
            log.error("News summary generation and store failed: {}", e.getMessage());
        }
    }

    public HashMap<String, String> generateNewsSummary() throws InterruptedException {
        HashMap<String, String> summary = new HashMap<>();
        List<String> categories = List.of(NewsCategoryCode.economy.name(),
                NewsCategoryCode.stock.name(),
                NewsCategoryCode.realestate.name());

        for (String category : categories) {
            List<String> newsArticles = newsCollector.collectNews(category);
            summary.put(category, geminiSummarize(newsArticles.toString()));
            log.info("{} summary done, waiting for 1 minute to avoid rate limit...", category);
            if(!category.equals(categories.getLast()))  sleep(60000); //1분 대기
        }

        return summary;
    }

    private String geminiSummarize(String News) {
        // The client gets the API key from the environment variable `GEMINI_API_KEY`.
        Builder builder = Client.builder();
        builder.apiKey(System.getenv("GEMINI_API_KEY"));
        Client client = builder.build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-pro",
                        "아래의 뉴스 기사들을 보고 반복적으로 나오거나 중요한 내용을 요약하시오."
                                + "요약한 내용 이외의 텍스트는 출력하지 말 것\n"
                                +News,
                        null);

        return response.text();
    }
}
