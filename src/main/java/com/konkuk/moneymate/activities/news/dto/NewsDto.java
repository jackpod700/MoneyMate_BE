package com.konkuk.moneymate.activities.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NewsDto {
    private String title;
    private String link;
    private String description;
    private String publisher;
    private String category;
    private String pubDate;
    private String author;
}
