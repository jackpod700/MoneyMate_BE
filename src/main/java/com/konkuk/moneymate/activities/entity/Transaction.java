package com.konkuk.moneymate.activities.entity;

import com.konkuk.moneymate.activities.dto.TransactionDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <h3>transaction의 Entity 클래스</h3>
 * <b>PK : transaction_no UUID</b><br>
 * <b>FK : bank_account_uid ref from bank_account.uid</b><br>
 * <b></b>
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "transaction_no", columnDefinition = "BINARY(16)", nullable = false)
    private UUID transactionNo;

    @ManyToOne
    @JoinColumn(name="bank_account_uid", nullable=false)
    private BankAccount bankAccount;

    @Column(name="counter_account", nullable = false)
    private String counterAccount;

    @Column(name = "outcome")
    private Integer outcome;

    @Column(name = "income")
    private Integer income;

    @Column(name="category", nullable = false)
    private String category;

    @Column(name="time", nullable = false)
    private LocalDateTime time;

    @Column(name="after_balance", nullable = false)
    private Long afterBalance;

    public Transaction(BankAccount bankAccount, String counterAccount, Integer outcome, Integer income, String category, LocalDateTime time, Long afterBalance) {
        this.bankAccount = bankAccount;
        this.counterAccount = counterAccount;
        this.outcome = outcome;
        this.income = income;
        this.category = category;
        this.time = time;
        this.afterBalance = afterBalance;
    }

    public TransactionDto toDto() {
        return new TransactionDto(
                this.time.toLocalDate(),
                this.time.toLocalTime(),
                this.outcome != null ? this.outcome : 0,
                this.income != null ? this.income : 0,
                this.afterBalance,
                this.counterAccount
        );
    }
}
