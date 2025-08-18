package com.konkuk.moneymate.activities.dto;

import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AssetHistoryDto {
    private YearMonth date;
    private String totalPrice;
}
