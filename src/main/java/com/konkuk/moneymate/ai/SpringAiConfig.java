package com.konkuk.moneymate.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;

@Configuration
public class SpringAiConfig {
    /**
     * application.yml 에 설정된 OpenAI(또는 Azure OpenAI) 설정을 사용.
     * spring.ai.openai.api-key, model 등은 이미 등록되어 있다고 하셨으니 생략합니다.
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
