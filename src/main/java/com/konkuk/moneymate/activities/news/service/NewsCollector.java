package com.konkuk.moneymate.activities.news.service;

import com.konkuk.moneymate.activities.news.dto.NewsDto;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NewsCollector {
    private final NewsService newsService;
    private final NewsExtractor newsExtractor;

    public NewsCollector(NewsService newsService, NewsExtractor newsExtractor) {
        this.newsService = newsService;
        this.newsExtractor = newsExtractor;
    }

    public List<String> collectNews(String category){
        List<NewsDto> newsDtos = newsService.getDetailNews("MK",category);
        List<String> newsContents = newsExtractor.extractArticleFromUrl(newsDtos);
        return newsContents;
    }
}
