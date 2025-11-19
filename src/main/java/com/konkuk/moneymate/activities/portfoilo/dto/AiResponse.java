package com.konkuk.moneymate.activities.portfoilo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AiResponse {
    private Integer userCount;
    private String context;
    private LocalDateTime currentTime;

    public static AiResponse of(Integer userCount, String context, LocalDateTime currentTime) {
        return new AiResponse(userCount, context, currentTime);
    }
}
