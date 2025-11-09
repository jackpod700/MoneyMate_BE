package com.konkuk.moneymate.activities.news.service;

import com.konkuk.moneymate.activities.news.dto.NewsDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class NewsExtractor {

    /**
     * Fetches HTML from a given URL and then extracts the article body.
     * @param newsDtos The full URL of the news article to scrape.
     * @return The extracted article text, or an error message if something goes wrong.
     */
    public List<String> extractArticleFromUrl(List<NewsDto> newsDtos) {
        List<String> contents = new ArrayList<>();
        for(NewsDto u: newsDtos){
            String link = u.getLink();
            if (link == null || link.isEmpty()) {
                System.err.println("The provided URL is malformed: " + newsDtos);
                continue;
            }
            try {
                Document doc = Jsoup.connect(link)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(5000)
                        .get();
                contents.add(parseArticleBody(doc));
            } catch (IllegalArgumentException e) {
                // 잘못된 URL 형식을 명확하게 처리
                System.err.println("Malformed URL provided: " + link);
            } catch (IOException e) {
                System.err.println("Error fetching the URL: " + link);
            }
        }
        return contents;
    }

    // parseArticleBody 메소드는 이전과 동일
    private String parseArticleBody(Document doc) {
        Element articleBody = doc.selectFirst("div[itemprop=articleBody]");
        if (articleBody == null) {
            return "Article body container not found on the page.";
        }
        StringBuilder contentBuilder = new StringBuilder();
        Element midTitle = articleBody.selectFirst(".mid_title .midtitle_text");
        if (midTitle != null) {
            midTitle.select("br").after("\n");
            contentBuilder.append(midTitle.text().trim()).append("\n\n");
        }
        Elements paragraphs = articleBody.select("p");
        for (Element p : paragraphs) {
            String paragraphText = p.text().trim();
            if (!paragraphText.isEmpty()) {
                contentBuilder.append(paragraphText).append("\n");
            }
        }
        return contentBuilder.toString().trim();
    }
}