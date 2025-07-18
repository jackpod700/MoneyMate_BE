package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.service.NewsService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
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
}
