package com.konkuk.moneymate.auth.api.service;

import com.konkuk.moneymate.auth.api.response.SmsMessageResponse;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <h3>MessageAuthService</h3>
 * <p>SMS 인증을 처리하는 서비스 (CoolSMS API 사용)</p>
 * <li><b>SMS 발송:</b> 4자리 인증번호를 사용자에게 전송</li>
 * <li><b>인증 확인:</b> 사용자가 입력한 인증번호 검증</li>
 * <li><b>Redis 저장:</b> 인증번호 및 userVerifyCode를 Redis에 TTL과 함께 저장</li>
 * <li><b>유효시간:</b> 인증번호 3분, userVerifyCode 5분</li>
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class MessageAuthService     {
    private static final long VERIFY_CODE_EXPIRE_SEC = 3 * 60; // 3분
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${coolsms.api.key}")
    private String apiKey;
    @Value("${coolsms.api.secret}")
    private String apiSecretKey;

    @PostConstruct
    public DefaultMessageService init() {
        return NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.solapi.com");
    }

    public Integer verifyCodeGenerator(){
        Random rand = new Random();
        int verifyNumber = rand.nextInt(8999) + 1000;
        return verifyNumber;
    }

    /**
     * <h3>SMS 인증번호 발송</h3>
     * <p>4자리 랜덤 인증번호를 생성하여 사용자에게 SMS로 전송합니다</p>
     * <li><b>1단계:</b> 4자리 랜덤 코드 생성 (1000-9999)</li>
     * <li><b>2단계:</b> CoolSMS API를 통해 SMS 발송</li>
     * <li><b>3단계:</b> 인증번호를 Redis에 저장 (3분 TTL)</li>
     * @param receiver 수신자 전화번호
     * @return ResponseEntity 200 OK (성공) 또는 500 (실패)
     */
    public ResponseEntity<?> smsSend(String receiver){
        DefaultMessageService messageService = init();
        Integer verifyCode = verifyCodeGenerator();

        // Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환하여 주세요
        Message message = new Message();
        message.setFrom("01040184834");
        message.setTo(receiver);
        message.setText("[MoneyMate] 인증번호 [" + verifyCode + "] 을 입력해 주세요.");


        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "SMS 발송 실패: " + exception.getMessage(),
                            null
                    ));
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "알 수 없는 오류: " + exception.getMessage(),
                            null
                    ));
        }

        /**
         * redis 저장 부분 추가
         */
        redisTemplate.opsForValue().set(receiver, String.valueOf(verifyCode), VERIFY_CODE_EXPIRE_SEC, TimeUnit.SECONDS);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.SMS_SEND_SUCCESS.getMessage(), message));
    }


    /**
     * <h3>SMS 인증번호 검증</h3>
     * <p>사용자가 입력한 인증번호를 검증하고 userVerifyCode를 발급합니다</p>
     * <li><b>1단계:</b> Redis에서 저장된 인증번호 조회</li>
     * <li><b>2단계:</b> 입력된 인증번호와 비교</li>
     * <li><b>3단계:</b> 인증 성공 시 userVerifyCode 생성 (UUID 12자리)</li>
     * <li><b>4단계:</b> userVerifyCode를 Redis에 저장 (5분 TTL)</li>
     * <li><b>5단계:</b> 기존 인증번호 삭제</li>
     * @param receiver 수신자 전화번호
     * @param verifyCode 사용자가 입력한 인증번호
     * @return ResponseEntity 200 OK (userVerifyCode 포함) 또는 400 (실패)
     */
    public ResponseEntity<?> smsVerify(String receiver, Integer verifyCode) {
        String savedCode = redisTemplate.opsForValue().get(receiver);

        Message message = new Message();
        message.setFrom("01040184834");
        message.setTo(receiver);
        message.setText("[MoneyMate] 인증번호 [" + verifyCode + "] 을 입력해 주세요.");

        if (savedCode == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("EXPIRED", "인증번호가 만료되었거나 존재하지 않습니다.", null));
        }

        if (!savedCode.equals(verifyCode.toString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("MISMATCH", "인증번호가 일치하지 않습니다.", null));
        }


        // 인증 성공 시 Redis 삭제
        redisTemplate.delete(receiver);

        String userVerifyCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        // String pwVerifyCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        long ID_VERIFY_CODE_EXPIRE_SEC = 5 * 60L;
        redisTemplate.opsForValue().set(receiver, userVerifyCode, ID_VERIFY_CODE_EXPIRE_SEC, TimeUnit.SECONDS);

        log.info("userVerifyCode 발급 완료. receiver={}, userVerifyCode={}", receiver, userVerifyCode);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.SMS_VERIFY_SUCCESS.getMessage(),
                SmsMessageResponse.of(receiver, verifyCode,"[MoneyMate] 인증 성공", userVerifyCode)));
    }
}


/*
return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            ApiResponseMessage.SMS_VERIFY_FAIL.getMessage(),
                            SmsMessageResponse.of(receiver, verifyCode, "[MoneyMate] 인증 실패")
                    ));
 */