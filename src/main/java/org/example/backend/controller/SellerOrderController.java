package org.example.backend.controller;

import org.example.backend.dto.seller.SellerOrderDTO;
import org.example.backend.model.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.SellerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller/orders")
public class SellerOrderController {

    @Autowired
    private SellerOrderService sellerOrderService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<SellerOrderDTO>> getSellerOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = getUser(userDetails);
        return ResponseEntity.ok(
                sellerOrderService.getSellerOrders(user.getUserId(), status, keyword, startDate, endDate, page, size));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SellerOrderDTO> updateOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        User user = getUser(userDetails);
        String status = body.get("status");
        return ResponseEntity.ok(sellerOrderService.updateOrderStatus(user.getUserId(), id, status));
    }

    @PostMapping("/bulk-status")
    public ResponseEntity<Void> bulkUpdateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {

        User user = getUser(userDetails);
        List<Long> orderIds = (List<Long>) body.get("orderIds"); // Warning: Check type safety in production
        String status = (String) body.get("status");

        // Convert integer list to long list if necessary (JSON arrays often come as
        // Integers)
        // Ignoring robust casting for speed here, assuming frontend sends valid Longs
        // or strict parsing handles it
        // Ideally use a DTO for body

        // Because generics are erased, we might need manual casting if Jackson
        // deserializes to Integer
        List<Long> safeOrderIds = orderIds.stream().map(obj -> ((Number) obj).longValue()).toList();

        sellerOrderService.bulkUpdateStatus(user.getUserId(), safeOrderIds, status);
        return ResponseEntity.ok().build();
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
