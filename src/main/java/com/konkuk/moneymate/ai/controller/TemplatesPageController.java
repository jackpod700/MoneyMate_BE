package com.konkuk.moneymate.ai.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplatesPageController {

    @GetMapping
    public String aiAgentPromptTemplate1(){
        return "ai/agent-template-page-ver1";
    }
}
