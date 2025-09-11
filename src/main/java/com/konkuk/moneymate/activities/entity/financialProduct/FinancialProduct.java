package com.konkuk.moneymate.activities.entity.financialProduct;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class FinancialProduct {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @ManyToOne
    @JoinColumn(name="financial_company_code", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FinancialCompany financialCompany;

    @Column(name="product_code", nullable = false)
    private String productCode;

    @Column(name="product_name", nullable = false)
    private String productName;

    @Column(name="join_way")
    private String joinWay;

    @Column(name="dcls_strt_day")
    private LocalDate disclosureStartDay;

    @Column(name="dcls_end_day")
    private LocalDate disclosureEndDay;
}
