package org.example.backend.controller;

import org.example.backend.dto.ProductReviewDTO;
import org.example.backend.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/product-reviews")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    @GetMapping
    public ResponseEntity<Page<ProductReviewDTO>> getAdminReviews(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer rating,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productReviewService.getAdminReviews(keyword, rating, pageable));
    }

    @PutMapping("/{id}/visibility")
    public ResponseEntity<Void> toggleReviewVisibility(@PathVariable Long id) {
        productReviewService.toggleReviewVisibility(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<Void> replyToReview(@PathVariable Long id, @RequestBody String reply) {
        // String reply might come as raw string or JSON. Assuming raw for simplicity or
        // simplistic JSON key extraction.
        // Usually, better to use DTO like ReplyRequest. But let's assume raw or simple
        // JSON.
        // Actually, @RequestBody String will get the body. If frontend sends { "reply":
        // "..." }, we need to parse or use DTO.
        // Let's use simple string for now, but handle potential JSON quotes if needed.
        // Assuming simple raw text for now.
        productReviewService.replyToReview(id, reply);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        productReviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}
