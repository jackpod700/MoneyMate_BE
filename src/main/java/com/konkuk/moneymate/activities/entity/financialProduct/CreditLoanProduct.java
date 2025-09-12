package com.konkuk.moneymate.activities.entity.financialProduct;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "credit_loan_product")
public class CreditLoanProduct extends FinancialProduct {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @Column(name = "crdt_prdt_type", length = 1)
    private char creditProductType;

    @Column(name = "crdt_prdt_type_nm", length = 20)
    private String creditProductTypeName;

    @Column(name = "cb_name", length = 10)
    private String cbName;

    //  mappedBy는 자식 엔티티에 있는 CreditLoanProduct 타입의 필드명을 가리킵니다.
    @OneToMany(mappedBy = "creditLoanProduct", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CreditLoanProductOption> options = new ArrayList<>();

    public CreditLoanProduct(
            UUID uid,
            FinancialCompany financialCompany,
            String productCode,
            String productName,
            String joinWay,
            char creditProductType,
            String creditProductTypeName,
            String cbName,
            LocalDate disclosureStartDay,
            LocalDate disclosureEndDay
    ) {
        super(uid, financialCompany, productCode, productName, joinWay, disclosureStartDay, disclosureEndDay);
        this.creditProductType = creditProductType;
        this.creditProductTypeName = creditProductTypeName;
        this.cbName = cbName;
    }

}