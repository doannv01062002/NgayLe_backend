package org.example.backend.controller;

import org.example.backend.dto.CreatePartnerRequest;
import org.example.backend.service.AffiliateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/affiliate")
public class AffiliateController {

    @Autowired
    private AffiliateService affiliateService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreatePartnerRequest request) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();
        affiliateService.register(userDetails.getUsername(), request.getSource());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();
        String status = affiliateService.getStatus(userDetails.getUsername());
        return ResponseEntity.ok(Collections.singletonMap("status", status));
    }
}
