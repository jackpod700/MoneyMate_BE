package com.konkuk.moneymate.auth;


import com.konkuk.moneymate.auth.application.AuthEntryPoint;
import com.konkuk.moneymate.auth.application.AuthenticationFilter;
import com.konkuk.moneymate.auth.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * <h3>SecurityConfig</h3>
 * <p>Spring Security 설정 클래스</p>
 * <li><b>JWT 기반 무상태(stateless) 인증</b></li>
 * <li><b>BCrypt 패스워드 인코딩</b></li>
 * <li><b>CORS 설정</b></li>
 * <li><b>공개 엔드포인트 정의 (PERMIT_ALL_PATTERNS)</b></li>
 * <li><b>커스텀 인증 필터 및 예외 핸들러 등록</b></li>
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationFilter authenticationFilter;
    private final AuthEntryPoint exceptionHandler;

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * <h3>PERMIT_ALL_PATTERNS</h3>
     * <p>권한 확인을 하지 않는 URI와 리소스 리스트</p>
     */
    private static final String[] PERMIT_ALL_PATTERNS = new String[] {
            "/h2-console/**", "/test",
            "/v1/api/member/**",
            "/v1/api/message/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v2/api-docs",
            "/user/find-id",
            "/user/reset-pw",
            "/swagger-resources/**",
            "/webjars/**",
            "/favicon.ico",
            "/user/reissue-token",
            "/health",
            "/user/verify/sms-send",
            "/user/verify/sms-request",
            "/test/page/stock",
            "/test/page/agent/**",
            "/test/page/agent/stream",
            "/css/**",
            "/images/**",
            "/js/**",
            "/api/proxy/naver-stock/**",
            "/api/proxy/eodhd/**"
    };

    /**
     * <h3>Security Filter Chain 설정</h3>
     * <p>HTTP 보안 설정을 구성합니다</p>
     * <li><b>CSRF 비활성화:</b> JWT 기반 인증이므로 불필요</li>
     * <li><b>세션 정책:</b> STATELESS - 서버 세션 사용 안 함</li>
     * <li><b>공개 엔드포인트:</b> PERMIT_ALL_PATTERNS에 정의된 URI는 인증 불필요</li>
     * <li><b>인증 필터:</b> UsernamePasswordAuthenticationFilter 앞에 AuthenticationFilter 등록</li>
     * <li><b>예외 처리:</b> 인증 실패 시 AuthEntryPoint로 처리</li>
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 중 발생하는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/asset/retirement/simulate").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user/check-id").permitAll()
                                .requestMatchers(PERMIT_ALL_PATTERNS).permitAll() // 수정

                                .anyRequest().authenticated())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(exceptionHandler))
                .logout(logout -> logout.logoutUrl("/spring-security-logout"));


        return http.build();
    }


    /**
     * <h3>CORS Configuration Source</h3>
     * <p>CORS Allow origin을 정의합니다. <br> HTTPS로 변경한다면 일부 수정될 수 있습니다</p>
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "/test/page/agent/**",
                "http://moneymate.s3-website.ap-northeast-2.amazonaws.com", // 예시
                "CORS Allow hosts"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 인증 필요 시 true

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}



    /*

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder().username("user").password(passwordEncoder().encode("password")).roles("USER").build();
        return new InMemoryUserDetailsManager(user);
    }

     */


// BCryptPasswordEncoder();