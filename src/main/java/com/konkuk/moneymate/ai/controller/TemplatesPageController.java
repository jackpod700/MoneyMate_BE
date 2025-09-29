package com.konkuk.moneymate.ai.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/test/page")
@Controller
public class TemplatesPageController {

    @GetMapping("/agent/v1")
    public String aiAgentPromptTemplate1(){
        return "ai/agent-template-page-ver1";
    }
}
