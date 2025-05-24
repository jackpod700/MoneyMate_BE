package com.konkuk.moneymate.activities.dto;

import java.util.UUID;
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
    private int accountBalance;

    public BankAccountDto(String accountName, String accountNumber, String accountBank, String accountType){
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountBank = accountBank;
        this.accountType = accountType;
    }
}
