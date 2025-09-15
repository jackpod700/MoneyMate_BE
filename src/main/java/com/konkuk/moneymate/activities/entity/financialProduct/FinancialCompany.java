package com.konkuk.moneymate.activities.entity.financialProduct;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="financial_company")
@AllArgsConstructor
@NoArgsConstructor
public class FinancialCompany {
    @Id
    @Column(name="code", length=7, nullable = false)
    private String code;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="fin_group_code", length=6, nullable = false)
    private String finGroupCode;

    @Column(name="home_url")
    private String homeUrl;

    @Column(name="call_num")
    private String callNum;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "financialCompany")
    private Set<FinancialCompanyRegion> financialCompanyRegions;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "financialCompany")
    private Set<DepositProduct> depositProducts;

    public FinancialCompany(String code, String name, String finGroupCode, String homeUrl, String callNum) {
        this.code = code;
        this.name = name;
        this.finGroupCode = finGroupCode;
        this.homeUrl = homeUrl;
        this.callNum = callNum;
    }
}
