package com.konkuk.moneymate.activities.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatsDataResponse
{
    private Integer age; // c1_nm
    private String c2; // c2_nm
    private String item; // item_nm
    private String itemName;
    private String year; // pre_de
    private String unitName;
    private Integer value; // value  : integer로 수정
}
