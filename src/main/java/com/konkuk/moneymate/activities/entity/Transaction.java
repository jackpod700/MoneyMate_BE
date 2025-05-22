package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class Transaction {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "transaction_no", columnDefinition = "BINARY(16)", nullable = false)
    private UUID transactionNo;

    @Column(name="counter_account", nullable = false)
    private String counterAccount;

    @Column(name = "out")
    private String out;

    @Column(name = "in")
    private String in;

    @Column(name="category", nullable = false)
    private String category;

    @Column(name="time", nullable = false)
    private LocalDateTime time;

    @Column(name="after_balance")
    private int afterBalance;

}
