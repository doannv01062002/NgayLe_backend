package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.response.SellerDashboardResponse;
import org.example.backend.model.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.SellerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller/dashboard")
@RequiredArgsConstructor
public class SellerDashboardController {

    private final SellerDashboardService sellerDashboardService;
    private final UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<SellerDashboardResponse> getDashboardStats(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "today") String period) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(sellerDashboardService.getDashboardStats(user.getUserId(), period));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
