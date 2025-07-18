package com.konkuk.moneymate.activities.enums;

import java.util.List;
import lombok.Getter;

@Getter
public enum NewsPublisher {

    KOREA_ECONOMY_ECONOMY("HK", NewsCategoryCode.economy.name(),"https://www.hankyung.com/feed/economy"),
    KOREA_ECONOMY_STOCK("HK", NewsCategoryCode.stock.name(),"https://www.hankyung.com/feed/finance"),
    KOREA_ECONOMY_REALESTATE("HK", NewsCategoryCode.realestate.name(),"https://www.hankyung.com/feed/realestate"),
    MAEIL_ECONOMY_ECONOMY("MK",NewsCategoryCode.economy.name(), "https://www.mk.co.kr/rss/30100041/"),
    MAEIL_ECONOMY_STOCK("MK",NewsCategoryCode.stock.name(), "https://www.mk.co.kr/rss/50200011/"),
    MAEIL_ECONOMY_REALESTATE("MK",NewsCategoryCode.realestate.name(), "https://www.mk.co.kr/rss/50300009/"),
    MAEIL_ECONOMY_BUSINESS("MK",NewsCategoryCode.business.name(), "https://www.mk.co.kr/rss/50100032/"),
    FINANCIAL_NEWS_ECONOMY("FNN", NewsCategoryCode.economy.name(), "https://www.fnnews.com/rss/r20/fn_realnews_economy.xml"),
    FINANCIAL_NEWS_STOCK("FNN", NewsCategoryCode.stock.name(), "https://www.fnnews.com/rss/r20/fn_realnews_stock.xml"),
    FINANCIAL_NEWS_FINANCE("FNN", NewsCategoryCode.finance.name(), "https://www.fnnews.com/rss/r20/fn_realnews_finance.xml"),
    FINANCIAL_NEWS_REALESTATE("FNN", NewsCategoryCode.realestate.name(), "https://www.fnnews.com/rss/r20/fn_realnews_realestate.xml"),
    YEONHAP_NEWS_ECONOMY("YN", NewsCategoryCode.economy.name(), "https://www.yna.co.kr/rss/economy.xml"),
    CHOSUN_ILBO_ECONOMY("CN", NewsCategoryCode.economy.name(), "https://www.chosun.com/arc/outboundfeeds/rss/category/economy/?outputType=xml"),
    HANGYEORE_ECONOMY("HG", NewsCategoryCode.economy.name(), "https://www.hani.co.kr/rss/economy"),
    YAHOO_FINANCE_ECONOMY("YF", NewsCategoryCode.economy.name(), "https://news.google.com/rss/search?q=when%3A24h%20allinurl%3Afinance.yahoo.com&hl=en-US&gl=US&ceid=US%3Aen"),
    FINANCIAL_TIMES_ECONOMY("FT", NewsCategoryCode.economy.name(), "https://news.google.com/rss/search?q=when%3A24h%20allinurl%3Aft.com&hl=en-US&gl=US&ceid=US%3Aen");


    private final String publisherCode;
    private final String categoryCode;
    private final String publisherURL;

    NewsPublisher(String publisherCode, String categoryCode, String publisherURL) {
        this.publisherCode = publisherCode;
        this.categoryCode = categoryCode;
        this.publisherURL = publisherURL;
    }

    public static String getUrlWithCodeandCategory(String code, String category) {
        for (NewsPublisher publisher : NewsPublisher.values()) {
            if (publisher.publisherCode.equals(code) && publisher.categoryCode.equals(category)) {
                return publisher.publisherURL;
            }
        }
        return null;
    }

    public static List<NewsPublisher> getPublishersWithCategory(String category) {
        return List.of(NewsPublisher.values()).stream()
                .filter(publisher -> publisher.categoryCode.equals(category))
                .toList();
    }
}
