package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.dto.news.NewsSummarizeDto;
import com.konkuk.moneymate.activities.entity.news.NewsSummarize;
import com.konkuk.moneymate.activities.repository.news.NewsSummarizeRepository;
import com.konkuk.moneymate.activities.service.NewsService;
import com.konkuk.moneymate.activities.service.news.NewsSummarizer;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {

    private final NewsService newsService;
    private final NewsSummarizer newsSummarizer;
    private final NewsSummarizeRepository newsSummarizeRepository;

    public NewsController(NewsService newsService, NewsSummarizer newsSummarizer,
                          NewsSummarizeRepository newsSummarizeRepository) {
        this.newsService = newsService;
        this.newsSummarizer = newsSummarizer;
        this.newsSummarizeRepository = newsSummarizeRepository;
    }

    @GetMapping("/news/all")
    public ResponseEntity<?> getTotalNews() {
        try {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.NEWS_TOTAL_SUCCESS.getMessage(),
                    newsService.getTotalNews()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                        e.getMessage()));
        }
    }

    @GetMapping("/news/detail")
    public ResponseEntity<?> getNewsDetail(@RequestParam("publisher") String publisher,
                                           @RequestParam("category") String category) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.NEWS_DETAIL_SUCCESS.getMessage(),
                    newsService.getDetailNews(publisher, category)));
        } catch (NullPointerException e) {
            return ResponseEntity.status(400).body(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    e.getMessage()));
        }
        catch(Exception e) {
            return ResponseEntity.status(500).body(
                    new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    e.getMessage()));
        }
    }

    @GetMapping("/news/ai-summary")
    public ResponseEntity<?> getNewsSummary() {
        try {
            // 가장 최근에 생성된 요약 데이터를 조회
            List<NewsSummarizeDto> latestSummary = newsSummarizeRepository
                    .findFirstOfAllCategoryOrderByGeneratedTimeDesc()
                    .stream().map(NewsSummarize::toDto).toList();

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.NEWS_SUMMARY_GET_SUCCESS.getMessage(),
                    latestSummary));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            e.getMessage()));
        }
    }

    // 테스트용 - 수동으로 요약 생성 및 저장
//    @GetMapping("/news/store-summary")
//    public ResponseEntity<?> storeNewsSummary() {
//        try {
//            HashMap<String, String> summaries = newsSummarizer.generateNewsSummary(); // category, summary
//            List<String> keys = summaries.keySet().stream().toList();
//            for (String key : keys) {
//                newsSummarizeRepository.save(new NewsSummarize(
//                        summaries.get(key),
//                        key));
//            }
//            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(
//                    new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                            e.getMessage()));
//        }
//    }
}
