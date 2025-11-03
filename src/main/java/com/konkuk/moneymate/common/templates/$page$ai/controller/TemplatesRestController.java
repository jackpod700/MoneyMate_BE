package com.konkuk.moneymate.common.templates.$page$ai.controller;

import com.konkuk.moneymate.common.templates.$page$ai.service.AgentTemplateService1;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/test/page")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TemplatesRestController {

    private final AgentTemplateService1 agentService;


    /**
     * 기본 스트림 응답
     */
    @PostMapping(value = "/agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponse(
            @RequestParam String statement,
            @RequestParam(defaultValue = "한국어") String language) {

        log.info("Stream request - statement: {}, language: {}", statement, language);
        return agentService.generateResponse(statement, language);
    }

    /**
     * 시스템 프롬프트를 포함한 스트림 응답
     */
    @PostMapping(value = "/agent/stream/system", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponseWithSystem(
            @RequestParam String statement,
            @RequestParam(defaultValue = "한국어") String language) {

        log.info("Stream with system request - statement: {}, language: {}", statement, language);
        return agentService.generateResponseWithSystem(statement, language);
    }

    /**
     * 커스텀 프롬프트를 사용한 스트림 응답
     */
    @PostMapping(value = "/agent/stream/custom", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponseWithCustom(
            @RequestParam String statement,
            @RequestParam(defaultValue = "한국어") String language) {

        log.info("Stream with custom prompt - statement: {}, language: {}", statement, language);
        return agentService.generateResponseWithCustomPrompt(statement, language);
    }

    /**
     * 전문가 역할을 가진 스트림 응답
     */
    @PostMapping(value = "/agent/stream/expert", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponseAsExpert(
            @RequestParam String statement,
            @RequestParam(defaultValue = "한국어") String language,
            @RequestParam(defaultValue = "IT") String expertRole) {

        log.info("Stream as expert - statement: {}, language: {}, role: {}",
                statement, language, expertRole);
        return agentService.generateResponseAsExpert(statement, language, expertRole);
    }
}
