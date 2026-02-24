package org.example.backend.controller;

import org.example.backend.dto.CreateOrderRequest;
import org.example.backend.dto.OrderDTO;
import org.example.backend.model.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(orderService.getUserOrders(user.getUserId()));
    }

    @PostMapping
    public ResponseEntity<List<OrderDTO>> createOrder(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateOrderRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(orderService.createOrder(user.getUserId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(orderService.getOrderById(user.getUserId(), id));
    }
}
