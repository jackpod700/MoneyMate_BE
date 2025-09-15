package com.konkuk.moneymate.activities.entity.financial.product;

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
@Table(name="saving_product")
@NoArgsConstructor
public class SavingProduct extends FinancialProduct {

    public SavingProduct(UUID uid,
                          FinancialCompany financialCompany,
                          String productCode,
                          String productName,
                          String joinWay,
                          LocalDate disclosureStartDay,
                          LocalDate disclosureEndDay,
                          String maturityInterest,
                          String specialCondition,
                          String joinDeny,
                          String joinMember,
                          String etcNote,
                          Long maxLimit) {
        super(uid, financialCompany, productCode, productName, joinWay, disclosureStartDay, disclosureEndDay);
        this.maturityInterest = maturityInterest;
        this.specialCondition = specialCondition;
        this.joinDeny = joinDeny;
        this.joinMember = joinMember;
        this.etcNote = etcNote;
        this.maxLimit = maxLimit;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "savingProduct")
    private Set<SavingProductOption> options;

    @Column(name="mtrt_int")
    private String maturityInterest;

    @Column(name="spcl_cnd", length=500)
    private String specialCondition;

    @Column(name="join_deny", length=1)
    private String joinDeny;

    @Column(name="join_member")
    private String joinMember;

    @Column(name="etc_note")
    private String etcNote;

    @Column(name="max_limit")
    private Long maxLimit;
}
