package com.konkuk.moneymate.activities.service.news;

import com.konkuk.moneymate.activities.news.service.NewsCollector;
import com.konkuk.moneymate.activities.news.service.NewsExtractor;
import com.konkuk.moneymate.activities.news.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewsCollectorTest {
    private NewsCollector newsCollector;

    @BeforeEach
    public void setUp() {
        NewsService newsService = new NewsService(); // You might want to mock this
        NewsExtractor newsExtractor = new NewsExtractor(); // You might want to mock this
        newsCollector = new NewsCollector(newsService, newsExtractor);
    }

    @Test
    public void testCollectNews() {
        newsCollector.collectNews();
    }
}
