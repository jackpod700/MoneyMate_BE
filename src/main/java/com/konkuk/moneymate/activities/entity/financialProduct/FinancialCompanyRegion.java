package com.konkuk.moneymate.activities.entity.financialProduct;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="financial_company_region")
@NoArgsConstructor
@AllArgsConstructor
public class FinancialCompanyRegion {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @ManyToOne
    @JoinColumn(name="financial_company_code", nullable=false)
    private FinancialCompany financialCompany;

    @Column(name="area_code", length=2, nullable = false)
    private String areaCode;

    @Column(name="area_name", nullable = false)
    private String areaName;

    public FinancialCompanyRegion(FinancialCompany financialCompany, String areaCode, String areaName) {
        this.financialCompany = financialCompany;
        this.areaCode = areaCode;
        this.areaName = areaName;
    }
}
