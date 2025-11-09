package com.konkuk.moneymate.activities.bankaccount;

import com.konkuk.moneymate.activities.bankaccount.dto.BankAccountDto;
import com.konkuk.moneymate.activities.bankaccount.enums.AccountType;
import com.konkuk.moneymate.activities.bankaccount.enums.BankCode;
import org.springframework.stereotype.Component;

@Component
public class BankAccountValidator {
    public void checkAccount(BankAccountDto account) {
        try{
            checkAccountNumber(account.getAccountNumber());
            checkAccountBank(account.getAccountBank());
            checkAccountType(account.getAccountType());
        }
        catch(IllegalArgumentException e){
            throw e;
        }

    }

    private void checkAccountNumber(String accountNumber){
        if (accountNumber == null || !accountNumber.matches("\\d{10,14}")) {
            throw new IllegalArgumentException("잘못된 계좌번호 형식입니다. 숫자 10~14자리여야 합니다.");
        }
    }

    private void checkAccountType(String accountType){
        try {
            AccountType.valueOf(accountType);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("유효하지 않은 계좌 유형입니다. '입출금' 또는 '예적금'만 허용됩니다.");
        }
    }
    private void checkAccountBank(String accountBank){
        if (!BankCode.exists(accountBank)) {
            throw new IllegalArgumentException("존재하지 않는 은행 코드입니다: " + accountBank);
        }
    }

}
