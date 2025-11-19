package com.konkuk.moneymate.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // RedisTemplate<String,String> 은 스프링 부트가 자동으로 빈으로 등록합니다.
    @Bean
    public ZSetOperations<String,String> zSetOps(RedisTemplate<String,String> template) {
        return template.opsForZSet();
    }
}