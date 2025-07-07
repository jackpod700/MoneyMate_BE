package com.konkuk.moneymate.auth.api.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * <h3>AuthTokenResponse  (DTO Class)</h3>
 * <p><b>Description  </b><br>로그인 성공하면 서버가 클라이언트에 보내는 Access, Response token의 DTO 역할</p>
 * <li><b>accessToken</b> </li>
 * <li><b>refreshToken</b> </li>
 * <li><b>grantType</b> </li>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokensResponse {

    private String accessToken;
    private String refreshToken;
    private String grantType;
    // private Long expiresIn;

    public static AuthTokensResponse of(String accessToken, String refreshToken, String grantType) {
        return new AuthTokensResponse(accessToken, refreshToken, grantType);
    }
}