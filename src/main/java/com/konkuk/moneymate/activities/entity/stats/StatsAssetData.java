package com.konkuk.moneymate.activities.entity.stats;

import jakarta.persistence.*;
import lombok.*;

/**
 * <h3>AssetStats (tb: stats_asset)</h3>
 * <li><strong>자산 통계를 위한 table entity</strong></li>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Entity
@Table(name="stats_data_asset")
public class StatsAssetData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="c1_obj_nm", length=127)
    private String c1ObjName;

    @Column(name="c2_obj_nm", length=127)
    private String c2ObjName;

    @Column(name="c1_nm", length=127)
    private String c1Name;

    @Column(name="c2_nm", length=127)
    private String c2Name;

    @Column(name="c1", length=63)
    private String c1;

    @Column(name="c2", length=63)
    private String c2;

    /**
     * value (DT) : String
     */
    @Column(name="value")
    private Double value;

    @Column(name="pre_de", length=63)
    private Integer year;

    @Column(name="item_nm", length=127)
    private String itemName;

    @Column(name="item_id", length=63)
    private String itemId;

    @Column(name="unit_nm", length=16)
    private String unitName;

}

/*
{
    v    분류명1              "C1_OBJ_NM": "가구특성별",
    v    분류값명(소분류)     "C2_NM": "자산",
    v    Value              "DT": "9882.2164383347",
        분류값ID            "C2": "C05",
        분류값ID            "C1": "B022",        (예시:29세 이하)
        수록주기            "PRD_SE": "A",
        단위영문명          "UNIT_NM_ENG": "10000won",  // 단위
        항목 ID            "ITM_ID": "T01",
        통계표ID           "TBL_ID": "DT_1HDAAD01",  // DT_1HDAAD01: 가구특성별 자산,부채
    v    항목명             "ITM_NM": "전가구 평균",
        통계표명           "TBL_NM": "가구특성별 자산·부채",
    v    수록시점           "PRD_DE": "2017",
        최종수정일          "LST_CHN_DE": "2024-12-02",
        분류값 영문         "C1_NM_ENG": "Less than 29 years old",
    v    분류값 명          "C1_NM": "(가구주 연령) 29세 이하",
        단위명             "UNIT_NM": "만원",
        항목영문명          "ITM_NM_ENG": "Average of all households",
        분류 영문명         "C2_OBJ_NM_ENG": "by household assets and liabilities",
        분류값 영문         "C2_NM_ENG": "Total assets",
        기관코드           "ORG_ID": "101",
        분류 영문명         "C1_OBJ_NM_ENG": "by household characteristics",
    v    분류명2             "C2_OBJ_NM": "자산 부채별"
    },
 */