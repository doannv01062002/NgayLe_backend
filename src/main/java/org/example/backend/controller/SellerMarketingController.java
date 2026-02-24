package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.response.SellerMarketingOverviewResponse;
import org.example.backend.model.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.SellerMarketingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/marketing")
@RequiredArgsConstructor
public class SellerMarketingController {

    private final SellerMarketingService sellerMarketingService;
    private final UserRepository userRepository;

    @GetMapping("/overview")
    public ResponseEntity<SellerMarketingOverviewResponse> getMarketingOverview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "30days") String period) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(sellerMarketingService.getMarketingOverview(user.getUserId(), period));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
