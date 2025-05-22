package com.konkuk.moneymate.activities.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@Entity
public class BankAccount {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @ManyToOne
    @JoinColumn(name="user_uid", nullable=false)
    private User user;

    @Column(name="account_number", nullable = false)
    private String accountNumber;

    @Column(name="naem", nullable = false)
    private String naem;

    @Column(name="bank", nullable = false)
    private String bank;

    @Column(name="current_balance", nullable = false)
    private int currentBalance;

    @Column(name="deposit_type", nullable = false)
    private String depositType;


}
