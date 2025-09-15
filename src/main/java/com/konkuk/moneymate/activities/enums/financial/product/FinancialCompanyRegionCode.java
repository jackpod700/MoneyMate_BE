package com.konkuk.moneymate.activities.enums.financial.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinancialCompanyRegionCode {
    SEOUL("seoul", "01", "서울"),
    BUSAN("busan", "02", "부산"),
    DAEGU("daegu", "03", "대구"),
    INCHEON("incheon", "04", "인천"),
    GWANGJU("gwangju", "05", "광주"),
    DAEJEON("daejeon", "06", "대전"),
    ULSAN("ulsan", "07", "울산"),
    SEJONG("sejong", "08", "세종"),
    GYEONGGI("gyeonggi", "09", "경기"),
    KANGWON("gangwon", "10", "강원"),
    CHUNGBUK("chungbuk", "11", "충북"),
    CHUNGNAM("chungnam", "12", "충남"),
    JEONBUK("jeonbuk", "13", "전북"),
    JEONNAM("jeonnam", "14", "전남"),
    GYEONGBUK("kyungbuk", "15", "경북"),
    GYEONGNAM("kyungnam", "16", "경남"),
    JEJU("jeju", "17", "제주");

    private final String requestCode;
    private final String code;
    private final String name;

    public static String fromRequestCodeToCode(String requestCode) {
        for (FinancialCompanyRegionCode region : FinancialCompanyRegionCode.values()) {
            if (region.getRequestCode().equals(requestCode)) {
                return region.getCode();
            }
        }
        throw new IllegalArgumentException("No matching FinancialCompanyRegionCode for requestCode: " + requestCode);
    }

}
