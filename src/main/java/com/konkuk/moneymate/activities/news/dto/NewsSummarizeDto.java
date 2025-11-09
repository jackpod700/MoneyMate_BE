package com.konkuk.moneymate.activities.news.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NewsSummarizeDto {
    private String category;
    private String content;
    private LocalDateTime generatedTime;
}
