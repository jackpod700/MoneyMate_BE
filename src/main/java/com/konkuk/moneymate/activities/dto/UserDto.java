package com.konkuk.moneymate.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
public class UserDto {
    private String userId;
    private String userName;

    /**
     * password:
     */
    private String password;
    private LocalDate birthday;
    private String phoneNumber;
}
