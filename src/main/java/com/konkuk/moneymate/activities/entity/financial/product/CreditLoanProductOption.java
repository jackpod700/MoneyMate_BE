package com.konkuk.moneymate.activities.entity.financial.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_loan_product_option")
public class CreditLoanProductOption {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    // 부모 엔티티와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_uid", referencedColumnName = "uid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CreditLoanProduct creditLoanProduct;

    @Column(name = "crdt_lend_rate_type", length = 1)
    private char creditLendRateType;

    @Column(name = "crdt_lend_rate_type_nm", length = 20)
    private String creditLendRateTypeName;

    // 정밀한 소수점 계산을 위해 Double 대신 BigDecimal을 사용합니다.
    @Column(name = "crdt_grad_1", precision = 5, scale = 2)
    private BigDecimal creditGrade1;

    @Column(name = "crdt_grad_4", precision = 5, scale = 2)
    private BigDecimal creditGrade4;

    @Column(name = "crdt_grad_5", precision = 5, scale = 2)
    private BigDecimal creditGrade5;

    @Column(name = "crdt_grad_6", precision = 5, scale = 2)
    private BigDecimal creditGrade6;

    @Column(name = "crdt_grad_10", precision = 5, scale = 2)
    private BigDecimal creditGrade10;

    @Column(name = "crdt_grad_11", precision = 5, scale = 2)
    private BigDecimal creditGrade11;

    @Column(name = "crdt_grad_12", precision = 5, scale = 2)
    private BigDecimal creditGrade12;

    @Column(name = "crdt_grad_13", precision = 5, scale = 2)
    private BigDecimal creditGrade13;

    @Column(name = "crdt_grad_avg", precision = 5, scale = 2)
    private BigDecimal creditGradeAvg;
}


