package com.konkuk.moneymate.activities.service.news;

import static java.lang.Thread.sleep;

import com.google.genai.Client;
import com.google.genai.Client.Builder;
import com.google.genai.types.GenerateContentResponse;
import com.konkuk.moneymate.activities.entity.news.NewsSummarize;
import com.konkuk.moneymate.activities.enums.NewsCategoryCode;
import com.konkuk.moneymate.activities.repository.news.NewsSummarizeRepository;
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
                        """
너는 전문 뉴스 애널리스트야.
아래에 제공되는 [뉴스 기사 묶음]을 읽고, 다음 [요약 포맷]에 맞춰서 하나의 종합적인 요약본을 한국어로 생성해 줘.
그리고 요약 내용 이외의 대답은 하지 마.

[요약 포맷]
# (핵심 주제 한줄 요약)

## 1. 전체 핵심 요약
(모든 기사를 관통하는 가장 중요한 결론 2-3 문장)

## 2. 주요 주제별 상세 요약
* **주제 1: (가장 중요한 첫 번째 주제)**
    * [주제 1 관련 상세 내용]
* **주제 2: (두 번째 주제)**
    * [주제 2 관련 상세 내용]
* **주제 3: (기타 주제)**
    * [주제 3 관련 상세 내용]

## 3. 주요 데이터
* **핵심 수치:** [기사에 언급된 중요 수치, 통계]

## 4. 시사점 및 향후 전망
(이 사건의 향후 영향이나 전망에 대한 분석 1-2 문장)

---
[뉴스 기사 묶음]"""
                                +News,
                        null);

        return response.text();
    }
}
