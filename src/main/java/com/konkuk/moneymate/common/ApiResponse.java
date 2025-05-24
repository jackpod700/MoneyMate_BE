package com.konkuk.moneymate.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String status; //사용예시: HttpStatus.OK.getReasonPhrase()
    private String message; //사용예시: ApiResponseMessage.ACCOUNT_REGISTER_SUCCESS.getMessage()
    private T data;

    public ApiResponse(String status, String message){
        this.status = status;
        this.message = message;
    }

}
