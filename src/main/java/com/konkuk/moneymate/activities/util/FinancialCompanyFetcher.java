package com.konkuk.moneymate.activities.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompany;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompanyRegion;
import com.konkuk.moneymate.activities.enums.financialProduct.FinancialGroupCode;
import com.konkuk.moneymate.activities.repository.financial.DepositProductOptionRepository;
import com.konkuk.moneymate.activities.repository.financial.DepositProductRepository;
import com.konkuk.moneymate.activities.repository.financial.FinancialCompanyRegionRepository;
import com.konkuk.moneymate.activities.repository.financial.FinancialCompanyRepository;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FinancialCompanyFetcher {

    final static String authKey = "9692682d8e9d25ea451a7cc1302df3ec";
    private final String baseUrl = "https://finlife.fss.or.kr/finlifeapi/";
    private final String authParam = "?auth="+authKey;
    private final String finGrpParam = "&topFinGrpNo=";
    private final String pageParam = "&pageNo=";
    private final FinancialCompanyRepository financialCompanyRepository;
    private final FinancialCompanyRegionRepository financialCompanyRegionRepository;
    private final DepositProductRepository depositProductRepository;
    private final DepositProductOptionRepository depositProductOptionRepository;

    public void scheduledFetchFinlifeInfo(){
        for(FinlifeFunctionParam param : FinlifeFunctionParam.values()){
            fetchFinlifeInfo(param.name());
        }
    }

    public void fetchFinlifeInfo(String category){
        String apiUrl = baseUrl+FinlifeFunctionParam.valueOf(category).getApiName()+authParam; //예시) https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json?auth=9692682d8e9d25ea451a7cc1302df3ec
        HttpClient client = HttpClient.newHttpClient();
        Class<?> baseInfo = FinlifeFunctionParam.valueOf(category).getBaseInfo();
        Class<?> optionInfo = FinlifeFunctionParam.valueOf(category).getOptionInfo();

        HashMap<String, Object> allBaseInfo = new HashMap<>();
        HashMap<String, List<Object>> allOptionInfo = new HashMap<>();

        for(FinancialGroupCode groupCode : FinancialGroupCode.values()){
            int currentPage = 1;
            String params=finGrpParam+groupCode.getCode()+pageParam+currentPage; //예시)&topFinGrpNo=020000&pageNo=1
            String url = apiUrl + params; //예시) https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json?auth=9692682d8e9d25ea451a7cc1302df3ec&topFinGrpNo=020000&pageNo=1
            try {
                // 3. API 요청 및 응답 수신
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // 4. body내용을 파싱
                Gson gson = new Gson();
                Type responseType = TypeToken.getParameterized(FinlifeApiResponse.class, baseInfo, optionInfo).getType();
                FinlifeApiResponse<?,?> parsedResponse = gson.fromJson(response.body(), responseType);

                // 5. 데이터 처리
                if (parsedResponse.getResult().getErrorCode().equals("000")) {
                    insertDatasToMap(parsedResponse, allBaseInfo, allOptionInfo, groupCode, category);
                }
                // 6. 페이지가 여러개인 경우 추가 요청

                for(;currentPage<parsedResponse.getResult().getMaxPageNo();currentPage++){
                    currentPage++;
                    params=finGrpParam+groupCode.getCode()+pageParam+currentPage;
                    url = apiUrl + params;
                    request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    parsedResponse = gson.fromJson(response.body(), responseType);
                    if (parsedResponse.getResult().getErrorCode().equals("000")) {
                        insertDatasToMap(parsedResponse, allBaseInfo, allOptionInfo, groupCode, category);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(FinlifeFunctionParam.valueOf(category).getExceptionMessage(), e);
            }
        }

        saveData(allBaseInfo, allOptionInfo, category);
    }

    private void insertDatasToMap(FinlifeApiResponse<?,?> parsedResponse,HashMap<String, Object> allBaseInfo, HashMap<String, List<Object>> allOptionInfo,FinancialGroupCode groupCode, String category) {
        if (category.equals(FinlifeFunctionParam.FINANCIAL_COMPANY.name())) {
            for (Object item : parsedResponse.getResult().getBaseList()) {
                FinancialCompanyBaseInfo baseInfo = (FinancialCompanyBaseInfo) item;
                allBaseInfo.put(baseInfo.getFinancialCompanyNo(), baseInfo.toEntity(groupCode.getCode()));
                allOptionInfo.put(baseInfo.getFinancialCompanyNo(), new ArrayList<>());
            }
            for (Object item : parsedResponse.getResult().getOptionList()) {
                FinancialCompanyOptionInfo optionInfo = (FinancialCompanyOptionInfo) item;
                if (allOptionInfo.containsKey(optionInfo.getFinancialCompanyNo())) {
                    allOptionInfo.get(optionInfo.getFinancialCompanyNo()).add(optionInfo);
                }
            }
            return;
        }
        if (category.equals(FinlifeFunctionParam.DEPOSIT_PRODUCT.name())) {
            for (Object item : parsedResponse.getResult().getBaseList()) {
                DepositProductBaseInfo baseInfo = (DepositProductBaseInfo) item;
                //deposit product는 financial company가 반드시 있어야함
                FinancialCompany financialCompany = financialCompanyRepository.findById(
                        baseInfo.getFinancialCompanyNo()).orElse(null);

                String key = baseInfo.getFinancialCompanyNo() + baseInfo.getFinancialProductCode();
                allBaseInfo.put(key, baseInfo.toEntity(financialCompany));
                allOptionInfo.put(key, new ArrayList<>());
            }
            for (Object item : parsedResponse.getResult().getOptionList()) {
                DepositProductOptionInfo optionInfo = (DepositProductOptionInfo) item;

                String key = optionInfo.getFinancialCompanyNo() + optionInfo.getFinancialProductCode();
                if (allOptionInfo.containsKey(key)) {
                    allOptionInfo.get(key).add(optionInfo);
                }
            }
            return;

        }
    }

    private void saveData(HashMap<String, Object> allBaseInfo, HashMap<String, List<Object>> allOptionInfo, String category) {
        if (category.equals(FinlifeFunctionParam.FINANCIAL_COMPANY.name())) {
            financialCompanyRepository.deleteAll();

            List<FinancialCompany> companies = allBaseInfo.values().stream()
                    .map(obj -> (FinancialCompany) obj) // 각 obj를 FinancialCompany로 형변환
                    .toList(); // Java 16+ 에서는 .toList() 사용, 이전 버전은 .collect(Collectors.toList())
            financialCompanyRepository.saveAll(companies);
            financialCompanyRegionRepository.deleteAll();
            for (String finCoNo : allOptionInfo.keySet()) {
                FinancialCompany financialCompany = (FinancialCompany) allBaseInfo.get(finCoNo);
                Set<FinancialCompanyRegion> regions = new HashSet<>();
                for (Object item : allOptionInfo.get(finCoNo)) {
                    FinancialCompanyOptionInfo optionInfo = (FinancialCompanyOptionInfo) item;
                    if (optionInfo.getExistsYn().equals("Y")) {
                        regions.add(optionInfo.toEntity(financialCompany));
                    }
                }
                financialCompanyRegionRepository.saveAll(regions);
            }
            return;
        }
        if (category.equals(FinlifeFunctionParam.DEPOSIT_PRODUCT.name())) {
            depositProductRepository.deleteAll();

            List<DepositProduct> products = allBaseInfo.values().stream()
                    .map(obj -> (DepositProduct) obj) // 각 obj를 DepositProduct로 형변환
                    .toList(); // Java 16+ 에서는 .toList() 사용, 이전 버전은 .collect(Collectors.toList())
            depositProductRepository.saveAll(products);
            depositProductOptionRepository.deleteAll();
            for (String key : allOptionInfo.keySet()) {
                DepositProduct depositProduct = (DepositProduct) allBaseInfo.get(key);
                Set<DepositProductOption> options = new HashSet<>();
                for (Object item : allOptionInfo.get(key)) {
                    DepositProductOptionInfo optionInfo = (DepositProductOptionInfo) item;
                    options.add(optionInfo.toEntity(depositProduct));
                }
                depositProductOptionRepository.saveAll(options);
            }
            return;
        }
    }

    public void fetchFinancialCompany(){
        String apiUrl = "https://finlife.fss.or.kr/finlifeapi/companySearch.json?auth="+authKey;
        HttpClient client = HttpClient.newHttpClient();

        HashMap<String, FinancialCompany> allBaseInfo = new HashMap<>();
        HashMap<String, List<FinancialCompanyOptionInfo>> allOptionInfo = new HashMap<>();

        for(FinancialGroupCode groupCode : FinancialGroupCode.values()){
            int currentPage = 1;
            String params="&topFinGrpNo="+groupCode.getCode()+"&pageNo="+currentPage;
            String url = apiUrl + params;
            try {
                // 3. API 요청 및 응답 수신
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // 4. body내용을 파싱
                Gson gson = new Gson();
                Type responseType = new TypeToken<FinlifeApiResponse<FinancialCompanyBaseInfo, FinancialCompanyOptionInfo>>() {}.getType();
                FinlifeApiResponse<FinancialCompanyBaseInfo,FinancialCompanyOptionInfo> financialCompanyResponse = gson.fromJson(response.body(), responseType);

                // 5. 데이터 처리
                if (financialCompanyResponse.getResult().getErrorCode().equals("000")) {
                    for(FinancialCompanyBaseInfo baseInfo : financialCompanyResponse.getResult().getBaseList()) {
                        allBaseInfo.put(baseInfo.getFinancialCompanyNo(), baseInfo.toEntity(groupCode.getCode()));
                        allOptionInfo.put(baseInfo.getFinancialCompanyNo(), new ArrayList<>());
                    }
                    for(FinancialCompanyOptionInfo optionInfo : financialCompanyResponse.getResult().getOptionList()) {
                        if(allOptionInfo.containsKey(optionInfo.getFinancialCompanyNo())) {
                            allOptionInfo.get(optionInfo.getFinancialCompanyNo()).add(optionInfo);
                        }
                    }
                }
                // 6. 페이지가 여러개인 경우 추가 요청
                for(;currentPage<financialCompanyResponse.getResult().getMaxPageNo();currentPage++){
                    params="&topFinGrpNo="+groupCode.getCode()+"&pageNo="+currentPage;
                    url = apiUrl + params;
                    request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    financialCompanyResponse = gson.fromJson(response.body(), responseType);
                    if (financialCompanyResponse.getResult().getErrorCode().equals("000")) {
                        for(FinancialCompanyBaseInfo baseInfo : financialCompanyResponse.getResult().getBaseList()) {
                            allBaseInfo.put(baseInfo.getFinancialCompanyNo(), baseInfo.toEntity(groupCode.getCode()));
                            allOptionInfo.put(baseInfo.getFinancialCompanyNo(), new ArrayList<>());
                        }
                        for(FinancialCompanyOptionInfo optionInfo : financialCompanyResponse.getResult().getOptionList()) {
                            if(allOptionInfo.containsKey(optionInfo.getFinancialCompanyNo())) {
                                allOptionInfo.get(optionInfo.getFinancialCompanyNo()).add(optionInfo);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("금융 회사 조회중 오류 발생: ", e);
            }

        }

        // 6. DB저장
        financialCompanyRepository.deleteAll();
        financialCompanyRepository.saveAll(allBaseInfo.values());
        financialCompanyRegionRepository.deleteAll();
        for(String finCoNo : allOptionInfo.keySet()){
            FinancialCompany financialCompany = allBaseInfo.get(finCoNo);
            Set<FinancialCompanyRegion> regions = new HashSet<>();
            for(FinancialCompanyOptionInfo optionInfo : allOptionInfo.get(finCoNo)){
                if(optionInfo.getExistsYn().equals("Y")){
                    regions.add(optionInfo.toEntity(financialCompany));
                }
            }
            financialCompanyRegionRepository.saveAll(regions);
        }
    }
}