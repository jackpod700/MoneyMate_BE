package com.konkuk.moneymate.activities.assets.dto;

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
