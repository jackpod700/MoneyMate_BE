package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.dto.financialProduct.DepositProductDto;
import com.konkuk.moneymate.activities.dto.financialProduct.MortgageLoanProductDto;
import com.konkuk.moneymate.activities.dto.financialProduct.RentHouseLoanProductDto;
import com.konkuk.moneymate.activities.dto.financialProduct.SavingProductDto;
import com.konkuk.moneymate.activities.entity.financialProduct.RentHouseLoanProduct;
import com.konkuk.moneymate.activities.service.FinancialProductService;
import com.konkuk.moneymate.activities.util.FinancialCompanyFetcher;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FinancialProductController {

    private final FinancialProductService financialProductService;
    private final FinancialCompanyFetcher financialCompanyFetcher;

    @GetMapping("/financial/products/deposit")
    public ResponseEntity<?> getDepositProducts(
            @RequestParam("savingAmount") int savingAmount,
            @RequestParam("period") int period,
            @RequestParam("finGrpCode") String finGrpCode,
            @RequestParam("region") String region,
            @RequestParam("intrType") String intrType,
            @RequestParam("joinDeny") String joinDeny,
            @RequestParam("joinWay") String joinWay
    ) {
        List<DepositProductDto> products = financialProductService.getDepositProducts(savingAmount, period, finGrpCode, region, intrType, joinDeny, joinWay);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.READ_FINANCIAL_PRODUCT_DEPOSIT_SUCCESS.getMessage(),
                products));
    }

    @GetMapping("/financial/products/saving")
    public ResponseEntity<?> getSavingProducts(
            @RequestParam("savingAmount") int savingAmount,
            @RequestParam("period") int period,
            @RequestParam("finGrpCode") String finGrpCode,
            @RequestParam("region") String region,
            @RequestParam("rsrvType") String rsrvType,
            @RequestParam("intrType") String intrType,
            @RequestParam("joinDeny") String joinDeny,
            @RequestParam("joinWay") String joinWay
    ) {
        List<SavingProductDto> products = financialProductService.getSavingProducts(savingAmount, period, finGrpCode, region, rsrvType, intrType, joinDeny, joinWay);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.READ_FINANCIAL_PRODUCT_SAVING_SUCCESS.getMessage(),
                products));
    }

    @GetMapping("/financial/products/mortgage-loan")
    public ResponseEntity<?> getMortgageLoanProducts(
            @RequestParam("mrtg_type") String mrtg_type,
            @RequestParam("finGrpCode") String finGrpCode,
            @RequestParam("region") String region,
            @RequestParam("rpayType") String rpayType,
            @RequestParam("lendRateType") String lendRateType,
            @RequestParam("joinWay") String joinWay
    ){
        List<MortgageLoanProductDto> products = financialProductService.getMortgageLoanProduct(mrtg_type, finGrpCode, region, rpayType, lendRateType, joinWay);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.READ_FINANCIAL_PRODUCT_MORTGAGE_LOAN_SUCCESS.getMessage(),
                products));
    }

    @GetMapping("/financial/products/rent-house-loan")
    public ResponseEntity<?> getRentHouseLoanProducts(
            @RequestParam("finGrpCode") String finGrpCode,
            @RequestParam("region") String region,
            @RequestParam("rpayType") String rpayType,
            @RequestParam("lendRateType") String lendRateType,
            @RequestParam("joinWay") String joinWay
    ){
        List<RentHouseLoanProductDto> products = financialProductService.getRentHouseLoanProduct(finGrpCode, region, rpayType, lendRateType, joinWay);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.READ_FINANCIAL_PRODUCT_MORTGAGE_LOAN_SUCCESS.getMessage(),
                products));
    }
//
//    @GetMapping("/financial/products/credit-loan")
//    public ResponseEntity<?> getCreditLoanProducts(
//
//    ){
//
//    }

    @GetMapping("/financial/fetchData")
    public void fetchFinancialData() {
        financialCompanyFetcher.scheduledFetchFinlifeInfo();
    }
}
