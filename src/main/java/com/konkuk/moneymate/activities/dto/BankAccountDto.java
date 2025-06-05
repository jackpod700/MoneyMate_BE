package com.konkuk.moneymate.activities.dto;

import java.util.UUID;

import com.konkuk.moneymate.activities.entity.BankAccount;
import com.konkuk.moneymate.activities.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BankAccountDto {
    private UUID accountUid;
    private String accountBank;
    private String accountName;
    private String accountType;
    private String accountNumber;
    private Long accountBalance;

    public BankAccountDto(String accountName, String accountNumber, String accountBank, String accountType){
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountBank = accountBank;
        this.accountType = accountType;
    }

    public BankAccount toEntity(User user) {
        return new BankAccount(
                user,
                this.accountBank,
                this.accountNumber,
                this.accountName,
                this.accountBalance,
                this.accountType
        );
    }
}
