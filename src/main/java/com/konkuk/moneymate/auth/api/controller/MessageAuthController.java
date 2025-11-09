package com.konkuk.moneymate.auth.api.controller;

import com.konkuk.moneymate.auth.api.request.SmsMessageRequest;
import com.konkuk.moneymate.auth.api.service.MessageAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h3>MessageAuthController</h3>
 * <p>SMS 인증을 처리하는 컨트롤러</p>
 * <li><b>POST /user/verify/sms-send:</b> SMS 인증번호 발송</li>
 * <li><b>POST /user/verify/sms-request:</b> SMS 인증번호 검증</li>
 */
@RequiredArgsConstructor
@RestController
public class MessageAuthController {

    private final MessageAuthService messageAuthService;

    /**
     * <h3>POST /user/verify/sms-send</h3>
     * <p>사용자에게 SMS 인증번호를 발송합니다</p>
     * <li><b>인증번호:</b> 4자리 랜덤 숫자</li>
     * <li><b>유효시간:</b> 3분</li>
     * @param smsMessageRequest 전화번호를 포함한 요청 본문
     * @return ResponseEntity 200 OK (발송 성공) 또는 500 (발송 실패)
     */
    @PostMapping("/user/verify/sms-send")
    public ResponseEntity<?> smsVerifyRequest(@RequestBody SmsMessageRequest smsMessageRequest) {
        String phoneNumber = smsMessageRequest.getPhoneNumber();
        String message = smsMessageRequest.getMessage();

        return messageAuthService.smsSend(phoneNumber);
    }

    /**
     * <h3>POST /user/verify/sms-request</h3>
     * <p>사용자가 입력한 SMS 인증번호를 검증하고 userVerifyCode를 발급합니다</p>
     * <li><b>1단계:</b> Redis에서 저장된 인증번호와 비교</li>
     * <li><b>2단계:</b> 일치하면 userVerifyCode 생성 (UUID 12자리)</li>
     * <li><b>3단계:</b> userVerifyCode를 Redis에 저장 (5분 유효)</li>
     * @param smsMessageRequest 전화번호 및 인증번호를 포함한 요청 본문
     * @return ResponseEntity 200 OK (userVerifyCode 포함) 또는 400 (검증 실패)
     */
    @PostMapping("/user/verify/sms-request")
    public ResponseEntity<?> smsRequest(@RequestBody SmsMessageRequest smsMessageRequest) {
        String phoneNumber = smsMessageRequest.getPhoneNumber();
        Integer verifyCode = smsMessageRequest.getVerifyCode();

        return messageAuthService.smsVerify(phoneNumber, verifyCode);
    }

}
