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
@Table(name="rent_house_loan_product_option")
@AllArgsConstructor
@NoArgsConstructor
public class RentHouseLoanProductOption {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RentHouseLoanProduct rentHouseLoanProduct;

    @Column(name="rpay_type")
    private char rpayType;

    @Column(name="rpay_type_nm", nullable = false)
    private String rpayTypeName;

    @Column(name="lend_rate_type")
    private char lendRateType;

    @Column(name="lend_rate_type_nm")
    private String lendRateTypeName;

    @Column(name="lend_rate_min")
    private BigDecimal lendRateMin;

    @Column(name="lend_rate_max")
    private BigDecimal lendRateMax;

    @Column(name="lend_rate_avg")
    private BigDecimal lendRateAvg;
}
