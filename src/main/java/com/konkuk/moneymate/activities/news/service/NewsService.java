package com.konkuk.moneymate.activities.news.service;

import com.konkuk.moneymate.activities.news.dto.NewsDto;
import com.konkuk.moneymate.activities.news.enums.NewsCategoryCode;
import com.konkuk.moneymate.activities.news.enums.NewsPublisher;
import com.konkuk.moneymate.common.ApiResponseMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class NewsService {

    public List<NewsDto> getTotalNews(){
        //각 뉴스사마다 economy category의 뉴스 4개씩 가져오기
        List<NewsDto> newsDtos = new ArrayList<>();
        List<NewsPublisher> newsPublishers = NewsPublisher.getPublishersWithCategory(NewsCategoryCode.economy.name());
        for(NewsPublisher newsPublisher : newsPublishers){
            try {
                String url = Objects.requireNonNull(newsPublisher.getPublisherURL());
                InputStream stream = makeConnection(url);

                // Jsoup를 사용하여 XML 파싱
                Elements items = getItemsFromStream(stream);

                // 각 item 요소에서 필요한 정보 추출
                int count = 0;
                for (Element item : items) {
                    newsDtos.add(makeNewsDtoFromItem(item, newsPublisher.getPublisherCode(), newsPublisher.getCategoryCode()));
                    count++;
                    if (count >= 4) {
                        break; // 각 뉴스사에서 4개만 가져오기
                    }
                }
            } catch(NullPointerException e){
                throw new NullPointerException(ApiResponseMessage.PUBLISHER_OR_CATEGORY_NOT_FOUND.getMessage());
            }
            catch (Exception e) {
                throw new RuntimeException("Error fetching news detail: " + e.getMessage(), e);
            }
        }
        return newsDtos;
    }

    public List<NewsDto> getDetailNews(String publisherCode, String categoryCode) {
        List<NewsDto> newsList = new ArrayList<>();
        try {
            String url = Objects.requireNonNull(NewsPublisher.getUrlWithCodeandCategory(publisherCode, categoryCode));
            InputStream stream = makeConnection(url);

            // Jsoup를 사용하여 XML 파싱
            Elements items = getItemsFromStream(stream);

            // 각 item 요소에서 필요한 정보 추출
            for (Element item : items) {
                newsList.add(makeNewsDtoFromItem(item, publisherCode, categoryCode));
            }
        }catch(NullPointerException e){
            throw new NullPointerException(ApiResponseMessage.PUBLISHER_OR_CATEGORY_NOT_FOUND.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching news detail: " + e.getMessage(), e);
        }
        return newsList;
    }

    private static InputStream makeConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
        connection.setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Referrer", "https://www.google.com");
        connection.setConnectTimeout(10 * 1000); // 연결 타임아웃 설정
        connection.setInstanceFollowRedirects(true); // 리디렉션 허용
        connection.connect();

        return connection.getInputStream();
    }

    private static Elements getItemsFromStream(InputStream stream) throws IOException {
        // Jsoup를 사용하여 XML 파싱
        Document doc = Jsoup.parse(stream, "UTF-8", "", org.jsoup.parser.Parser.xmlParser());
        return doc.select("item");
    }

    private static NewsDto makeNewsDtoFromItem(Element item, String publisherCode, String categoryCode) {
        String title = item.selectFirst("title") != null ? item.selectFirst("title").text() : "";
        String link = item.selectFirst("link") != null ? item.selectFirst("link").text() : "";
        String description = item.selectFirst("description") != null ? item.selectFirst("description").text() : "";
        String pubDate = item.selectFirst("pubDate") != null ? item.selectFirst("pubDate").text() : "";
        Element authorEl = item.selectFirst("author");

        return new NewsDto(
                title,
                link,
                description,
                publisherCode,
                categoryCode,
                pubDate,
                authorEl != null ? authorEl.text() : ""
        );
    }
}
