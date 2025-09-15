package com.konkuk.moneymate.activities.entity.financialProduct;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@Table(name="rent_house_loan_product")
@NoArgsConstructor
public class RentHouseLoanProduct extends FinancialProduct {
    public RentHouseLoanProduct(UUID uid,
                               FinancialCompany financialCompany,
                               String productCode,
                               String productName,
                               String joinWay,
                               String loanInciExpn,
                               String earlyRepayFee,
                               String delayRate,
                               String loanLimit,
                               LocalDate disclosureStartDay,
                               LocalDate disclosureEndDay) {
        super(uid, financialCompany, productCode, productName, joinWay, disclosureStartDay, disclosureEndDay);
        this.loanInciExpn = loanInciExpn;
        this.earlyRepayFee = earlyRepayFee;
        this.delayRate = delayRate;
        this.loanLimit = loanLimit;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rentHouseLoanProduct")
    private Set<RentHouseLoanProductOption> options;

    @Column(name="loan_inci_expn")
    private String loanInciExpn;

    @Column(name="erly_rpay_fee")
    private String earlyRepayFee;

    @Column(name="dly_rate")
    private String delayRate;

    @Column(name="loan_lmt")
    private String loanLimit;

}
