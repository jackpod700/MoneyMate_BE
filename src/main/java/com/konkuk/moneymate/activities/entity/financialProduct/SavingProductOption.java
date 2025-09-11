package com.konkuk.moneymate.activities.entity.financialProduct;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="saving_product_option")
@AllArgsConstructor
@NoArgsConstructor
public class SavingProductOption {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SavingProduct savingProduct;

    @Column(name="rsrv_type")
    private char rsrvType;

    @Column(name="rsrv_type_nm")
    private String rsrvTypeName;

    @Column(name="intr_rate_type")
    private char intrRateType;

    @Column(name="intr_rate_type_nm")
    private String intrRateTypeName;

    @Column(name="save_trm")
    private Integer saveTerm;

    @Column(name="intr_rate")
    private BigDecimal intrRate;

    @Column(name="intr_rate2")
    private BigDecimal intrRate2;
}
