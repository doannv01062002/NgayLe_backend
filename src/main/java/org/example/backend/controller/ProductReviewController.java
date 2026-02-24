package org.example.backend.controller;

import org.example.backend.dto.ProductReviewDTO;
import org.example.backend.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-reviews")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ProductReviewDTO>> getReviewsByProduct(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productReviewService.getReviewsByProduct(productId, pageable));
    }

    @PostMapping
    public ResponseEntity<ProductReviewDTO> createReview(
            @RequestBody ProductReviewDTO dto,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(productReviewService.createProductReview(dto, userDetails.getUsername()));
    }
}
