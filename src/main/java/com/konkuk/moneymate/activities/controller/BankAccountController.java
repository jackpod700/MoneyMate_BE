package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.dto.BankAccountDto;
import com.konkuk.moneymate.activities.dto.TransactionDto;
import com.konkuk.moneymate.activities.service.BankAccountService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/account/register")
    public ResponseEntity<?> registerAccount(@RequestBody Map<String,Object> body){
        String accountNumber = body.get("accountNumber").toString();
        String accountName = body.get("accountName").toString();
        String accountType = body.get("accountType").toString();
        String accountBank = body.get("accountBank").toString();

        BankAccountDto accountDto = new BankAccountDto(accountName, accountNumber, accountBank, accountType);
        String userUid = body.get("userUid").toString();
        try{
            bankAccountService.registerAccount(accountDto, userUid);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.getReasonPhrase(),e.getMessage()));
        }
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.ACCOUNT_REGISTER_SUCCESS.getMessage(), accountDto));
    }

    @GetMapping("/account/get-list")
    public ResponseEntity<?> getAccountList(){
        UUID userUid= UUID.randomUUID(); //jwt user uid 들어가면 수정
        List<BankAccountDto> bankAccountList = bankAccountService.getAccountList(userUid);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("account", bankAccountList);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.ACCOUNT_QUERY_SUCCESS.getMessage(), responseData));
    }

    @PostMapping("/account/get-transaction")
    public ResponseEntity<?> getTransaction(@RequestBody Map<String,Object> body){
        String accountUid = body.get("accountUid").toString();
        LocalDate startDate, endDate;
        try{
            startDate = LocalDate.parse(body.get("startDate").toString());
            endDate = LocalDate.parse(body.get("endDate").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            ApiResponseMessage.WRONG_FORMAT.getMessage()));
        }

        UUID userUid= UUID.randomUUID();
        List<TransactionDto> transactionList = bankAccountService.getTransactionList(accountUid, userUid.toString(), startDate, endDate);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("transaction", transactionList);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.TRANSACTION_QUERY_SUCCESS.getMessage(), responseData));
    }

    @PostMapping("/account/delete")
    public ResponseEntity<?> deleteAccount(@RequestBody Map<String,Object> body){
        String accountUid = body.get("accountUid").toString();
        
        return ResponseEntity.ok().build();
    }
}
