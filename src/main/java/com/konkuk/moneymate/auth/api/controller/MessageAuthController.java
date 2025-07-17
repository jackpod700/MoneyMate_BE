package com.konkuk.moneymate.auth.api.controller;

import com.konkuk.moneymate.auth.api.request.SmsMessageRequest;
import com.konkuk.moneymate.auth.service.MessageAuthService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import net.nurigo.sdk.message.request.SingleMessageSendingRequest;

@RequiredArgsConstructor
@RestController
public class MessageAuthController {

    private final MessageAuthService messageAuthService;


    @PostMapping("/user/verify/sms-send")
    public ResponseEntity<?> smsVerifyRequest(@RequestBody SmsMessageRequest smsMessageRequest) {
        String phoneNumber = smsMessageRequest.getPhoneNumber();
        String message = smsMessageRequest.getMessage();

        return messageAuthService.smsSend(phoneNumber);
    }

}
