package com.konkuk.moneymate.activities.service;

import com.konkuk.moneymate.activities.dto.BankAccountDto;
import com.konkuk.moneymate.activities.dto.TransactionDto;
import com.konkuk.moneymate.activities.validator.BankAccountValidator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

    private final BankAccountValidator bankAccountValidator;

    public BankAccountService(BankAccountValidator bankAccountValidator) {
        this.bankAccountValidator = bankAccountValidator;
    }


    public void registerAccount(BankAccountDto accountDto) {
        bankAccountValidator.checkAccount(accountDto);
        //entity 생성후 repository로 전달

    }

    public List<BankAccountDto> getAccountList(UUID userUid) {
        List<BankAccountDto> bankAccountList = new ArrayList<>();

        BankAccountDto bankAccountDto = new BankAccountDto(UUID.randomUUID(),"asdf","12355","004","입출금",0);
        bankAccountList.add(bankAccountDto);
        bankAccountDto = new BankAccountDto(UUID.randomUUID(),"asdddf","123512155","004","입출금",10000);
        bankAccountList.add(bankAccountDto);

        return bankAccountList;
    }

    public List<TransactionDto> getTransactionList(String accountUid, String userUid, LocalDate startDate, LocalDate endDate) {
        List<TransactionDto> transactionList = new ArrayList<>();
        //해당 유저가 가지고 있는 계좌인지 확인

        //예제 데이터
        TransactionDto transactionDto1 = new TransactionDto(LocalDate.now(), LocalTime.NOON,1000,0,10000,"씨유편의점");
        TransactionDto transactionDto2 = new TransactionDto(LocalDate.now(), LocalTime.now(),2000,0,10000,"씨유편의점");
        transactionList.add(transactionDto1);
        transactionList.add(transactionDto2);

        return transactionList;
    }
}
