package org.example.backend.controller;

import org.example.backend.dto.CartDTO;
import org.example.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDTO> addToCart(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        Long variantId = ((Number) body.get("variantId")).longValue();
        Integer quantity = ((Number) body.get("quantity")).intValue();
        return ResponseEntity.ok(cartService.addToCart(userId, variantId, quantity));
    }

    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartDTO> updateQuantity(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        Integer quantity = body.get("quantity");
        return ResponseEntity.ok(cartService.updateQuantity(userId, itemId, quantity));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartDTO> removeFromCart(@PathVariable Long userId, @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, itemId));
    }
}
