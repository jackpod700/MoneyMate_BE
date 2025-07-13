package com.konkuk.moneymate.auth.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HealthController {

    @GetMapping("health")
    public ResponseEntity<String> elbCheck() {
        return ResponseEntity.ok().body("Check Success!!");
    }
}
