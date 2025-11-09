package com.konkuk.moneymate.activities.bankaccount.service;

import com.konkuk.moneymate.activities.bankaccount.dto.BankAccountDto;
import com.konkuk.moneymate.activities.bankaccount.dto.TransactionDto;
import com.konkuk.moneymate.activities.bankaccount.entity.BankAccount;
import com.konkuk.moneymate.activities.user.entity.User;
import com.konkuk.moneymate.activities.bankaccount.repository.BankAccountRepository;
import com.konkuk.moneymate.activities.bankaccount.repository.TransactionRepository;
import com.konkuk.moneymate.activities.user.repository.UserRepository;
import com.konkuk.moneymate.activities.bankaccount.BankAccountValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

    private final BankAccountValidator bankAccountValidator;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public BankAccountService(BankAccountValidator bankAccountValidator, UserRepository userRepository, BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository) {
        this.bankAccountValidator = bankAccountValidator;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }



    public void registerAccount(BankAccountDto accountDto, String userUid) {
        bankAccountValidator.checkAccount(accountDto);
        User user = userRepository.findByUid(UUID.fromString(userUid))
                .orElseThrow(() -> new EntityNotFoundException(ApiResponseMessage.USER_NOT_FOUND.getMessage()));

        //entity 생성후 repository로 전달
        BankAccount bankAccount = accountDto.toEntity(user);
        bankAccountRepository.save(bankAccount);
    }



    public List<BankAccountDto> getAccountList(String userUid) {
        List<BankAccountDto> bankAccountList = new ArrayList<>();
        bankAccountRepository.findByUser_Uid(UUID.fromString(userUid))
                .forEach(bankAccount -> {
                    BankAccountDto bankAccountDto = bankAccount.toDto();
                    bankAccountList.add(bankAccountDto);
                });

        return bankAccountList;
    }

    public List<TransactionDto> getTransactionList(String accountUid, String userUid, LocalDate startDate, LocalDate endDate) throws IllegalAccessException {
        List<TransactionDto> transactionList = new ArrayList<>();
        //해당 유저가 가지고 있는 계좌인지 확인
        BankAccount bankAccount = bankAccountRepository.findById(UUID.fromString(accountUid))
                .orElseThrow(() -> new EntityNotFoundException(ApiResponseMessage.ACCOUNT_NOT_FOUND.getMessage()));
        if (!bankAccount.getUser().getUid().toString().equals(userUid)) {
            throw new IllegalAccessException(ApiResponseMessage.NO_ACCESS_AUTHORITY.getMessage());
        }
        //해당 계좌의 거래내역을 가져오는 로직
        transactionRepository.findByBankAccountUidAndTimeBetween(UUID.fromString(accountUid), startDate.atStartOfDay(), endDate.atStartOfDay())
                .forEach(transaction -> {
                    TransactionDto transactionDto = transaction.toDto();
                    transactionList.add(transactionDto);
                });
        return transactionList;
    }

    public void delete(String userUid, String accountUid) throws IllegalAccessException {
        BankAccount bankAccount = bankAccountRepository.findById(UUID.fromString(accountUid))
                .orElseThrow(() -> new EntityNotFoundException(ApiResponseMessage.ACCOUNT_NOT_FOUND.getMessage()));
        if (!bankAccount.getUser().getUid().toString().equals(userUid)) {
            throw new IllegalAccessException(ApiResponseMessage.NO_ACCESS_AUTHORITY.getMessage());
        }

        bankAccountRepository.deleteById(UUID.fromString(accountUid));
    }
}
