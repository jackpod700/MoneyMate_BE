package com.konkuk.moneymate.activities.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

/**
 * <h3>bank_account의 Entity 클래스</h3>
 * <b>PK : uid UUID</b><br>
 * <b>FK : user_uid ref from user.uid</b>
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="bank_account")
public class BankAccount {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    // FK user_uid

    @ManyToOne
    @JoinColumn(name="user_uid", nullable=false)
    private User user;

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "bankAccount")
    @JsonIgnore
    private List<Transaction> transactions;



    @Column(name="account_number", nullable = false)
    private String accountNumber;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="bank", nullable = false)
    private String bank;

    @Column(name="current_balance", nullable = false)
    private Integer currentBalance;

    @Column(name="deposit_type", nullable = false)
    private String depositType;

    public BankAccount(User user,  String bank, String accountNumber, String name, Integer currentBalance, String depositType) {
        this.user = user;
        this.accountNumber = accountNumber;
        this.name = name;
        this.bank = bank;
        this.currentBalance = currentBalance;
        this.depositType = depositType;
    }


}
