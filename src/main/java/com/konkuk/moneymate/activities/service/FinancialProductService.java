package com.konkuk.moneymate.activities.service;

import com.konkuk.moneymate.activities.dto.financialProduct.DepositProductDto;
import com.konkuk.moneymate.activities.dto.financialProduct.FinancialProductDto;
import com.konkuk.moneymate.activities.dto.financialProduct.MortgageLoanProductDto;
import com.konkuk.moneymate.activities.dto.financialProduct.SavingProductDto;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompany;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialProduct;
import com.konkuk.moneymate.activities.enums.financialProduct.FinancialCompanyRegionCode;
import com.konkuk.moneymate.activities.enums.financialProduct.FinancialGroupCode;
import com.konkuk.moneymate.activities.enums.financialProduct.InterestType;
import com.konkuk.moneymate.activities.enums.financialProduct.JoinWay;
import com.konkuk.moneymate.activities.enums.financialProduct.LendRateType;
import com.konkuk.moneymate.activities.enums.financialProduct.RpayType;
import com.konkuk.moneymate.activities.enums.financialProduct.RsrvType;
import com.konkuk.moneymate.activities.repository.financial.DepositProductRepository;
import com.konkuk.moneymate.activities.repository.financial.FinancialCompanyRepository;
import com.konkuk.moneymate.activities.repository.financial.MortgageLoanProductRepository;
import com.konkuk.moneymate.activities.repository.financial.SavingProductRepository;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FinancialProductService {

    private final FinancialCompanyRepository financialCompanyRepository;
    private final DepositProductRepository depositProductRepository;
    private final SavingProductRepository savingProductRepository;
    private final MortgageLoanProductRepository mortgageLoanProductRepository;

    public List<DepositProductDto> getDepositProducts(int savingAmount, int period, String finGrpCode, String region, String intrType, String joinDeny, String joinWay) {
        //savingAmount보다 작은 금액, period와 같은 기간, finGrpCode와 같은 금융그룹코드, region을 포함하는 지역, intrType과 같은 이자계산방식, joinMember를 포함, joinWay를 포함하는 상품들을 products에 추가

        List<FinancialCompany> financialCompanyList = getFinancialCompanysByRegionAndFinGrp(finGrpCode, region);

        //금융회사와 저축금액, 기간, 가입제한에 따른 예적금 상품 가져오기
        List<DepositProductDto> depositProductDtoList = depositProductRepository.findByFinancialCompanyCodeAndSavingAmountAndSaveTrmAndJoinDeny(
                financialCompanyList.stream().map(FinancialCompany::getCode).toList(),
                savingAmount,
                period,
                joinDeny
        );

        //이자계산방식 조건에 맞추어 데이터 필터링
        if(!intrType.equals("all")){
            depositProductDtoList.removeIf(depositProductDto -> !depositProductDto.getIntrType().equals(InterestType.fromCode(intrType)));
        }

        //가입방법 조건에 맞추어 데이터 필터링
        filterByJoinWay(depositProductDtoList, joinWay);

        return depositProductDtoList;
    }

    public List<SavingProductDto> getSavingProducts(int savingAmount, int period, String finGrpCode, String region, String rsrvType, String intrType, String joinDeny, String joinWay) {
        //savingAmount보다 작은 금액, period와 같은 기간, finGrpCode와 같은 금융그룹코드, region을 포함하는 지역, intrType과 같은 이자계산방식, joinMember를 포함, joinWay를 포함하는 상품들을 products에 추가

        List<FinancialCompany> financialCompanyList = getFinancialCompanysByRegionAndFinGrp(finGrpCode, region);

        //금융회사와 저축금액, 기간, 가입제한에 따른 예적금 상품 가져오기
        List<SavingProductDto> savingProductDtoList = savingProductRepository.findByFinancialCompanyCodeAndSavingAmountAndSaveTrmAndJoinDeny(
                financialCompanyList.stream().map(FinancialCompany::getCode).toList(),
                savingAmount,
                period,
                joinDeny
        );

        //이자계산방식 조건에 맞추어 데이터 필터링
        if(!intrType.equals("all")){
            savingProductDtoList.removeIf(savingProductDto -> !savingProductDto.getIntrType().equals(InterestType.fromCode(intrType)));
        }

        if(!rsrvType.equals("all")){
            savingProductDtoList.removeIf(savingProductDto -> !savingProductDto.getRsrvtype().equals(RsrvType.fromCode(rsrvType)));
        }

        //가입방법 조건에 맞추어 데이터 필터링
        filterByJoinWay(savingProductDtoList, joinWay);

        return savingProductDtoList;
    }

    public List<MortgageLoanProductDto> getMortgageLoanProduct(String mrtgType, String finGrpCode, String region, String rpayType, String lendRateType, String joinWay) {
        List<FinancialCompany> financialCompanyList = getFinancialCompanysByRegionAndFinGrp(finGrpCode, region);

        //금융회사와 담보유형에 따라 상품가져오기
        List<MortgageLoanProductDto> mortgageLoanProductDtoList = mortgageLoanProductRepository.findByFinancialCompanyCodeAndMrtgType(
                financialCompanyList.stream().map(FinancialCompany::getCode).toList(),
                mrtgType.charAt(0)
                );

        //상환방식 조건에 맞춰 상품 필터링
        if(!rpayType.equals("all")){
            mortgageLoanProductDtoList.removeIf(productDto -> !productDto.getRpayType().equals(RpayType.fromCode(rpayType)));
        }

        //금리방식에 맞춰 상품 피렅링
        if(!lendRateType.equals("all")){
            mortgageLoanProductDtoList.removeIf(productDto -> !productDto.getLendRateType().equals(LendRateType.fromCode(lendRateType)));
        }

        //가입방법 조건에 맞추어 데이터 필터링
        filterByJoinWay(mortgageLoanProductDtoList, joinWay);

        return mortgageLoanProductDtoList;
    }

    private List<FinancialCompany> getFinancialCompanysByRegionAndFinGrp(String finGrpCode, String region){
        //지역에 따른 금융회사 가져오기
        List<FinancialCompany> financialCompanyList;
        if(!region.equals("all")){
            financialCompanyList = financialCompanyRepository.findCompaniesByAreaCodes(Stream.of(region.split(",")).map(
                    FinancialCompanyRegionCode::fromRequestCodeToCode).toList());
        }
        else{
            financialCompanyList = financialCompanyRepository.findAll();
        }

        //금융권역코드 조건에 맞추어 데이터 필터링
        if(!finGrpCode.equals("all")){
            financialCompanyList.removeIf(financialcompany -> !financialcompany.getFinGroupCode()
                    .equals(FinancialGroupCode.fromRequestCodeToCode(finGrpCode)));
        }
        return financialCompanyList;
    }

    private <T extends FinancialProductDto> void filterByJoinWay(List<T> financialProductDtoList, String joinWay){
        //가입방법 조건에 맞추어 데이터 필터링
        if(!joinWay.equals("all")){
            List<String> joinWays = List.of(joinWay.split(","));
            financialProductDtoList.removeIf(financialProductDto -> {
                for(String jw : joinWays){
                    if(financialProductDto.getJoinWay().contains(JoinWay.findByName(jw))){
                        return false;
                    }
                }
                return true;
            });
        }
    }


}
