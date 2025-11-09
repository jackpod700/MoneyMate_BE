package com.konkuk.moneymate.activities.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class KosisStatsDataResponse
{
    private Integer age; // c1_nm
    private String c2; // c2_nm
    private String item; // item_nm
    private String itemName;
    private String year; // pre_de
    private String unitName;
    private Integer value; // value  : integer로 수정
}
