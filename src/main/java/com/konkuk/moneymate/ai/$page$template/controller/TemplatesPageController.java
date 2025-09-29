package com.konkuk.moneymate.ai.$page$template.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/test/page")
@Controller
@RequiredArgsConstructor
@Slf4j
public class TemplatesPageController {

    @GetMapping("/agent/v1")
    public String aiAgentPromptTemplate1(){
        log.info("Loading agent template page v1");
        return "ai/agent-template-page-ver1";
    }
}
