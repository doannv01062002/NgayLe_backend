package org.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.backend.utils.ResponseUtil;

@RestController
@RequestMapping("/api/public")
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("Server is running", "OK"));
    }
}
