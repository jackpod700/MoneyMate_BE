package com.konkuk.moneymate.common.templates.$page$ai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <h3>TemplatesPageController</h3>
 * <p>AI Agent 테스트 페이지를 서빙하는 컨트롤러</p>
 * <li><b>GET /test/page/agent/v1:</b> AI Agent 테스트 페이지 v1 (기본 에이전트)</li>
 * <li><b>GET /test/page/agent/v2:</b> AI Agent 테스트 페이지 v2 (자산 분석)</li>
 * <li><b>GET /test/page/agent/v3:</b> AI Agent 테스트 페이지 v3 (포트폴리오 분석)</li>
 */
@RequestMapping("/test/page")
@Controller
@RequiredArgsConstructor
@Slf4j
public class TemplatesPageController {

    /**
     * <h3>GET /test/page/agent/v1</h3>
     * <p>AI Agent 기본 테스트 페이지를 표시합니다</p>
     * @return Thymeleaf 템플릿 이름 "ai/agent-template-page-ver1"
     */
    @GetMapping("/agent/v1")
    public String aiAgentPromptTemplate1(){
        log.info("Loading agent template page v1");
        return "ai/agent-template-page-ver1";
    }

    /**
     * <h3>GET /test/page/agent/v2</h3>
     * <p>AI Agent 자산 분석 테스트 페이지를 표시합니다</p>
     * @return Thymeleaf 템플릿 이름 "ai/agent-template-page-ver2"
     */
    @GetMapping("/agent/v2")
    public String aiAgentPromptTemplate2(){
        log.info("Loading agent template page v2");
        return "ai/agent-template-page-ver2";
    }

    /**
     * <h3>GET /test/page/agent/v3</h3>
     * <p>AI Agent 포트폴리오 분석 테스트 페이지를 표시합니다</p>
     * @return Thymeleaf 템플릿 이름 "ai/agent-template-page-ver3"
     */
    @GetMapping("/agent/v3")
    public String aiAgentPromptTemplate3(){
        log.info("Loading agent template page v3");
        return "ai/agent-template-page-ver3";
    }
}
