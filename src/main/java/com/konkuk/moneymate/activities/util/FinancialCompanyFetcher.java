package com.konkuk.moneymate.activities.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konkuk.moneymate.activities.entity.financialProduct.CreditLoanProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.CreditLoanProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompany;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompanyRegion;
import com.konkuk.moneymate.activities.entity.financialProduct.MortgageLoanProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.MortgageLoanProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.RentHouseLoanProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.RentHouseLoanProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.SavingProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.SavingProductOption;
import com.konkuk.moneymate.activities.enums.financialProduct.FinancialGroupCode;
import com.konkuk.moneymate.activities.repository.financial.CreditLoanProductOptionRepository;
import com.konkuk.moneymate.activities.repository.financial.CreditLoanProductRepository;
import com.konkuk.moneymate.activities.repository.financial.DepositProductOptionRepository;
import com.konkuk.moneymate.activities.repository.financial.DepositProductRepository;
import com.konkuk.moneymate.activities.repository.financial.FinancialCompanyRegionRepository;
import com.konkuk.moneymate.activities.repository.financial.FinancialCompanyRepository;
import com.konkuk.moneymate.activities.repository.financial.MortgageLoanProductOptionRepository;
import com.konkuk.moneymate.activities.repository.financial.MortgageLoanProductRepository;
import com.konkuk.moneymate.activities.repository.financial.RentHouseLoanProductOptionRepository;
import com.konkuk.moneymate.activities.repository.financial.RentHouseLoanProductRepository;
import com.konkuk.moneymate.activities.repository.financial.SavingProductOptionRepository;
import com.konkuk.moneymate.activities.repository.financial.SavingProductRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
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
    private final SavingProductRepository savingProductRepository;
    private final SavingProductOptionRepository savingProductOptionRepository;
    private final MortgageLoanProductRepository mortgageLoanProductRepository;
    private final MortgageLoanProductOptionRepository mortgageLoanProductOptionRepository;
    private final RentHouseLoanProductRepository rentHouseLoanProductRepository;
    private final RentHouseLoanProductOptionRepository rentHouseLoanProductOptionRepository;
    private final CreditLoanProductRepository creditLoanProductRepository;
    private final CreditLoanProductOptionRepository creditLoanProductOptionRepository;

    /**
     * 매일 새벽 3시에 cron 표현식에 따라 자동으로 실행됩니다.
     * cron = "초 분 시 일 월 요일"
     * "0 0 3 * * *" : 매일 3시 0분 0초에 실행
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void scheduledFetchFinlifeInfo() {
        // --- 1. 작업 시작 로그 ---
        log.info("✅ [SCHEDULE START] 금융 상품 정보 업데이트 스케줄을 시작합니다.");

        for (FinlifeFunctionParam param : FinlifeFunctionParam.values()) {
            // --- 2. 작업 진행 로그 ---
            log.info("▶️ Fetching data for category: {}", param.name());
            try {
                fetchFinlifeInfo(param.name());
            } catch (Exception e) {
                // --- 3. 개별 작업 실패 로그 ---
                log.error("❌ Category '{}' 처리 중 에러 발생: {}", param.name(), e.getMessage());
            }
        }

        // --- 4. 작업 완료 로그 ---
        log.info("✅ [SCHEDULE END] 금융 상품 정보 업데이트 스케줄을 완료했습니다.");
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
                //product는 financial company가 반드시 있어야함
                FinancialCompany financialCompany = financialCompanyRepository.findById(
                        baseInfo.getFinancialCompanyNo()).orElse(null);
                if(financialCompany == null) {
                    continue;
                }
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
        if(category.equals(FinlifeFunctionParam.SAVING_PRODUCT.name())){
            for (Object item : parsedResponse.getResult().getBaseList()) {
                SavingProductBaseInfo baseInfo = (SavingProductBaseInfo) item;
                //product는 financial company가 반드시 있어야함
                FinancialCompany financialCompany = financialCompanyRepository.findById(
                        baseInfo.getFinancialCompanyNo()).orElse(null);
                if(financialCompany == null) {
                    continue;
                }
                String key = baseInfo.getFinancialCompanyNo() + baseInfo.getFinancialProductCode();
                allBaseInfo.put(key, baseInfo.toEntity(financialCompany));
                allOptionInfo.put(key, new ArrayList<>());
            }
            for (Object item : parsedResponse.getResult().getOptionList()) {
                SavingProductOptionInfo optionInfo = (SavingProductOptionInfo) item;

                String key = optionInfo.getFinancialCompanyNo() + optionInfo.getFinancialProductCode();
                if (allOptionInfo.containsKey(key)) {
                    allOptionInfo.get(key).add(optionInfo);
                }
            }
            return;
        }
        if(category.equals(FinlifeFunctionParam.MORTGAGE_LOAN_PRODUCT.name())){
            for (Object item : parsedResponse.getResult().getBaseList()) {
                MortgageLoanProductBaseInfo baseInfo = (MortgageLoanProductBaseInfo) item;
                //product는 financial company가 반드시 있어야함
                FinancialCompany financialCompany = financialCompanyRepository.findById(
                        baseInfo.getFinancialCompanyNo()).orElse(null);
                if(financialCompany == null) {
                    continue;
                }
                String key = baseInfo.getFinancialCompanyNo() + baseInfo.getFinancialProductCode();
                allBaseInfo.put(key, baseInfo.toEntity(financialCompany));
                allOptionInfo.put(key, new ArrayList<>());
            }
            for (Object item : parsedResponse.getResult().getOptionList()) {
                MortgageLoanProductOptionInfo optionInfo = (MortgageLoanProductOptionInfo) item;

                String key = optionInfo.getFinancialCompanyNo() + optionInfo.getFinancialProductCode();
                if (allOptionInfo.containsKey(key)) {
                    allOptionInfo.get(key).add(optionInfo);
                }
            }
            return;
        }
        if(category.equals(FinlifeFunctionParam.RENT_HOUSE_LOAN_PRODUCT.name())){
            for (Object item : parsedResponse.getResult().getBaseList()) {
                RentHouseLoanProductBaseInfo baseInfo = (RentHouseLoanProductBaseInfo) item;
                //product는 financial company가 반드시 있어야함
                FinancialCompany financialCompany = financialCompanyRepository.findById(
                        baseInfo.getFinancialCompanyNo()).orElse(null);
                if(financialCompany == null) {
                    continue;
                }
                String key = baseInfo.getFinancialCompanyNo() + baseInfo.getFinancialProductCode();
                allBaseInfo.put(key, baseInfo.toEntity(financialCompany));
                allOptionInfo.put(key, new ArrayList<>());
            }
            for (Object item : parsedResponse.getResult().getOptionList()) {
                RentHouseLoanProductOptionInfo optionInfo = (RentHouseLoanProductOptionInfo) item;

                String key = optionInfo.getFinancialCompanyNo() + optionInfo.getFinancialProductCode();
                if (allOptionInfo.containsKey(key)) {
                    allOptionInfo.get(key).add(optionInfo);
                }
            }
            return;
        }
        if(category.equals(FinlifeFunctionParam.CREDIT_LOAN_PRODUCT.name())){
            for (Object item : parsedResponse.getResult().getBaseList()) {
                CreditLoanProductBaseInfo baseInfo = (CreditLoanProductBaseInfo) item;
                //product는 financial company가 반드시 있어야함
                FinancialCompany financialCompany = financialCompanyRepository.findById(
                        baseInfo.getFinancialCompanyNo()).orElse(null);
                if(financialCompany == null) {
                    continue;
                }
                String key = baseInfo.getFinancialCompanyNo() + baseInfo.getFinancialProductCode();
                allBaseInfo.put(key, baseInfo.toEntity(financialCompany));
                allOptionInfo.put(key, new ArrayList<>());
            }
            for (Object item : parsedResponse.getResult().getOptionList()) {
                CreditLoanProductOptionInfo optionInfo = (CreditLoanProductOptionInfo) item;

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
        if(category.equals(FinlifeFunctionParam.SAVING_PRODUCT.name())){
            // SavingProduct 저장 로직 구현 (DepositProduct와 유사)
            savingProductRepository.deleteAll();

            List<SavingProduct> products = allBaseInfo.values().stream()
                    .map(obj -> (SavingProduct) obj) // 각 obj를 DepositProduct로 형변환
                    .toList(); // Java 16+ 에서는 .toList() 사용, 이전 버전은 .collect(Collectors.toList())
            savingProductRepository.saveAll(products);
            savingProductOptionRepository.deleteAll();
            for (String key : allOptionInfo.keySet()) {
                SavingProduct savingProduct = (SavingProduct) allBaseInfo.get(key);
                Set<SavingProductOption> options = new HashSet<>();
                for (Object item : allOptionInfo.get(key)) {
                    SavingProductOptionInfo optionInfo = (SavingProductOptionInfo) item;
                    options.add(optionInfo.toEntity(savingProduct));
                }
                savingProductOptionRepository.saveAll(options);
            }
            return;
        }
        if(category.equals(FinlifeFunctionParam.MORTGAGE_LOAN_PRODUCT.name())){
            // MortgageLoanProduct 저장 로직 구현 (DepositProduct와 유사)
            mortgageLoanProductRepository.deleteAll();

            List<MortgageLoanProduct> products = allBaseInfo.values().stream()
                    .map(obj -> (MortgageLoanProduct) obj) // 각 obj를 DepositProduct로 형변환
                    .toList(); // Java 16+ 에서는 .toList() 사용, 이전 버전은 .collect(Collectors.toList())
            mortgageLoanProductRepository.saveAll(products);
            mortgageLoanProductOptionRepository.deleteAll();
            for (String key : allOptionInfo.keySet()) {
                MortgageLoanProduct mortgageLoanProduct = (MortgageLoanProduct) allBaseInfo.get(key);
                Set<MortgageLoanProductOption> options = new HashSet<>();
                for (Object item : allOptionInfo.get(key)) {
                    MortgageLoanProductOptionInfo optionInfo = (MortgageLoanProductOptionInfo) item;
                    options.add(optionInfo.toEntity(mortgageLoanProduct));
                }
                mortgageLoanProductOptionRepository.saveAll(options);
            }
            return;
        }
        if(category.equals(FinlifeFunctionParam.RENT_HOUSE_LOAN_PRODUCT.name())){
            // MortgageLoanProduct 저장 로직 구현 (DepositProduct와 유사)
            rentHouseLoanProductRepository.deleteAll();

            List<RentHouseLoanProduct> products = allBaseInfo.values().stream()
                    .map(obj -> (RentHouseLoanProduct) obj) // 각 obj를 DepositProduct로 형변환
                    .toList(); // Java 16+ 에서는 .toList() 사용, 이전 버전은 .collect(Collectors.toList())
            rentHouseLoanProductRepository.saveAll(products);
            rentHouseLoanProductOptionRepository.deleteAll();
            for (String key : allOptionInfo.keySet()) {
                RentHouseLoanProduct rentHouseLoanProduct = (RentHouseLoanProduct) allBaseInfo.get(key);
                Set<RentHouseLoanProductOption> options = new HashSet<>();
                for (Object item : allOptionInfo.get(key)) {
                    RentHouseLoanProductOptionInfo optionInfo = (RentHouseLoanProductOptionInfo) item;
                    options.add(optionInfo.toEntity(rentHouseLoanProduct));
                }
                rentHouseLoanProductOptionRepository.saveAll(options);
            }
            return;
        }
        if(category.equals(FinlifeFunctionParam.CREDIT_LOAN_PRODUCT.name())){
            // MortgageLoanProduct 저장 로직 구현 (DepositProduct와 유사)
            creditLoanProductRepository.deleteAll();

            List<CreditLoanProduct> products = allBaseInfo.values().stream()
                    .map(obj -> (CreditLoanProduct) obj) // 각 obj를 DepositProduct로 형변환
                    .toList(); // Java 16+ 에서는 .toList() 사용, 이전 버전은 .collect(Collectors.toList())
            creditLoanProductRepository.saveAll(products);
            creditLoanProductOptionRepository.deleteAll();
            for (String key : allOptionInfo.keySet()) {
                CreditLoanProduct creditLoanProduct = (CreditLoanProduct) allBaseInfo.get(key);
                Set<CreditLoanProductOption> options = new HashSet<>();
                for (Object item : allOptionInfo.get(key)) {
                    CreditLoanProductOptionInfo optionInfo = (CreditLoanProductOptionInfo) item;
                    options.add(optionInfo.toEntity(creditLoanProduct));
                }
                creditLoanProductOptionRepository.saveAll(options);
            }
            return;
        }
    }
}