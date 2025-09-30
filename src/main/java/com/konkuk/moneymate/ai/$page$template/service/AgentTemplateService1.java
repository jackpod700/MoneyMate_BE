package com.konkuk.moneymate.ai.$page$template.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AgentTemplateService1 {
    // ##### 필드 #####
    private ChatClient chatClient;

    private PromptTemplate systemTemplate = SystemPromptTemplate.builder()
            .template("""
          당신은 친절하고 전문적인 AI 어시스턴트입니다.
          사용자의 질문에 명확하고 정확하게 답변해주세요.
          답변은 markdown 형식으로 구조화되게 작성해주세요.
          """)
            .build();

    private PromptTemplate userTemplate = PromptTemplate.builder()
            .template("다음 질문에 대한 답변을 {language}로 답변해주고 markdown 형식으로 요약을 해주세요.\n질문: {statement}")
            .build();

    // ##### 생성자 #####
    public AgentTemplateService1(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        log.info("AgentTemplateService1 initialized");
    }

    // ##### 메소드 #####

    /**
     * 기본 프롬프트 템플릿을 사용한 스트림 응답
     */
    public Flux<String> generateResponse(String statement, String language) {
        log.debug("generateResponse called - statement: {}, language: {}", statement, language);

        Prompt prompt = userTemplate.create(
                Map.of("statement", statement, "language", language));

        Flux<String> response = chatClient.prompt(prompt)
                .stream()
                .content();

        return response;
    }

    /**
     * 시스템 메시지와 사용자 메시지를 함께 사용하는 스트림 응답
     */
    public Flux<String> generateResponseWithSystem(String statement, String language) {
        log.debug("generateResponseWithSystem called - statement: {}, language: {}", statement, language);

        Flux<String> response = chatClient.prompt()
                .system(systemTemplate.render())
                .user(userTemplate.render(Map.of("statement", statement, "language", language)))
                .stream()
                .content();

        return response;
    }

    /**
     * render 메소드를 사용한 스트림 응답
     */
    public Flux<String> generateResponseWithRender(String statement, String language) {
        log.debug("generateResponseWithRender called - statement: {}, language: {}", statement, language);

        Flux<String> response = chatClient.prompt()
                .system(systemTemplate.render())
                .user(userTemplate.render(Map.of("statement", statement, "language", language)))
                .stream()
                .content();

        return response;
    }

    /**
     * 문자열 포맷을 사용한 커스텀 프롬프트 스트림 응답
     */
    public Flux<String> generateResponseWithCustomPrompt(String statement, String language) {
        log.debug("generateResponseWithCustomPrompt called - statement: {}, language: {}", statement, language);

        String systemText = """
        당신은 전문적인 AI 어시스턴트입니다.
        사용자의 질문을 분석하고 체계적으로 답변해주세요.
        필요한 경우 예시를 들어 설명해주세요.
        """;

        String userText = """
        다음 질문에 %s로 답변해주세요.
        
        질문: %s
        
        답변 작성 시 주의사항:
        1. 핵심 내용을 먼저 제시
        2. 필요시 상세 설명 추가
        3. markdown 형식으로 작성
        """.formatted(language, statement);

        Flux<String> response = chatClient.prompt()
                .system(systemText)
                .user(userText)
                .stream()
                .content();

        return response;
    }

    /**
     * 특정 역할을 가진 AI로 동작하는 스트림 응답
     */
    public Flux<String> generateResponseAsExpert(String statement, String language, String expertRole) {
        log.debug("generateResponseAsExpert called - statement: {}, language: {}, role: {}",
                statement, language, expertRole);

        String systemText = """
        당신은 %s 전문가입니다.
        전문적인 관점에서 정확하고 신뢰할 수 있는 정보를 제공해주세요.
        """.formatted(expertRole);

        String userText = """
        다음 질문에 %s로 답변해주세요.
        질문: %s
        """.formatted(language, statement);

        Flux<String> response = chatClient.prompt()
                .system(systemText)
                .user(userText)
                .stream()
                .content();

        return response;
    }
}