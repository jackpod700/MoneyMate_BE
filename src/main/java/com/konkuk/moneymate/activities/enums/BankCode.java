package com.konkuk.moneymate.activities.enums;

public enum BankCode {
    BANK_OF_KOREA("001", "한국은행"),
    KDB("002", "산업은행"),
    IBK("003", "기업은행"),
    KB("004", "국민은행"),
    KEB("005", "외환은행"),
    SUHYUP("007", "수협은행"),
    KEXIM("008", "수출입은행"),
    NH("011", "농협은행"),
    NH_MEMBER("012", "농협회원조합"),
    WOORI("020", "우리은행"),
    SC("023", "SC제일은행"),
    SEOUL_BANK("026", "서울은행"),
    CITI("027", "한국씨티은행"),
    IM_BANK("031", "iM뱅크(대구)"),
    BUSAN("032", "부산은행"),
    GWANGJU("034", "광주은행"),
    JEJU("035", "제주은행"),
    JEONBUK("037", "전북은행"),
    GYEONGNAM("039", "경남은행"),
    SAE_MAEUL("045", "새마을금고연합회"),
    SHINHYEOP("048", "신협중앙회"),
    SAVINGS_BANK("050", "상호저축은행"),
    FOREIGN("051", "기타 외국계은행"),
    MORGAN_STANLEY("052", "모건스탠리은행"),
    HSBC("054", "HSBC은행"),
    DEUTSCHE("055", "도이치은행"),
    RBS("056", "알비에스피엘씨은행"),
    JPMORGAN("057", "제이피모간체이스은행"),
    MIZUHO("058", "미즈호코퍼레이트은행"),
    MUFG("059", "미쓰비시도쿄UFJ은행"),
    BOA("060", "BOA"),
    BNP("061", "비엔피파리바은행"),
    ICBC("062", "중국공상은행"),
    BOC("063", "중국은행"),
    FOREST("064", "산림조합"),
    DAEHWA("065", "대화은행"),
    POST("071", "우체국"),
    KODIT("076", "신용보증기금"),
    KIBO("077", "기술신용보증기금"),
    HANA("081", "하나은행"),
    SHINHAN("088", "신한은행"),
    K_BANK("089", "케이뱅크"),
    KAKAO_BANK("090", "카카오뱅크"),
    TOSS_BANK("092", "토스뱅크"),
    HF("093", "한국주택금융공사"),
    SGI("094", "서울보증보험"),
    POLICE("095", "경찰청"),
    KFTC("099", "금융결제원"),
    DONGYANG("209", "동양종합금융증권"),
    HYUNDAI_SEC("218", "현대증권"),
    MIRAED("230", "미래에셋증권"),
    DAEWOO("238", "대우증권"),
    SAMSUNG("240", "삼성증권"),
    KOREA_INV("243", "한국투자증권"),
    NH_INV("247", "NH투자증권"),
    KYOBO("261", "교보증권"),
    HI_INV("262", "하이투자증권"),
    HMC("263", "에이치엠씨투자증권"),
    KIWOOM("264", "키움증권"),
    ETRADE("265", "이트레이드증권"),
    SK("266", "SK증권"),
    DAE_SHIN("267", "대신증권"),
    SOLOMON("268", "솔로몬투자증권"),
    HANHWA("269", "한화증권"),
    HANA_INV("270", "하나대투증권"),
    TOSS_INV("271", "토스증권"),
    SHINHAN_INV("278", "신한금융투자"),
    DONG_BU("279", "동부증권"),
    EUGENE("280", "유진투자증권"),
    MERITZ("287", "메리츠증권"),
    NH_ALT("289", "엔에이치투자증권"),
    BUKUK("290", "부국증권"),
    SHINYOUNG("291", "신영증권"),
    LIG("292", "엘아이지투자증권");

    private final String code;
    private final String name;

    BankCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static boolean exists(String inputCode) {
        for (BankCode b : BankCode.values()) {
            if (b.code.equals(inputCode)) {
                return true;
            }
        }
        return false;
    }
}
