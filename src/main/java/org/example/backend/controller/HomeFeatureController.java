package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.HomeFeatureProductDTO;
import org.example.backend.service.HomeFeatureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeFeatureController {

    private final HomeFeatureService homeFeatureService;

    // --- ADMIN ENDPOINTS ---
    @GetMapping("/api/v1/admin/home-features")
    public ResponseEntity<Page<HomeFeatureProductDTO>> getFeatures(
            @RequestParam(defaultValue = "TODAYS_SUGGESTION") String sectionType,
            Pageable pageable) {
        return ResponseEntity.ok(homeFeatureService.getFeatureProducts(sectionType, pageable));
    }

    @PostMapping("/api/v1/admin/home-features")
    public ResponseEntity<Void> addProduct(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "TODAYS_SUGGESTION") String sectionType) {
        homeFeatureService.addProductToSection(productId, sectionType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/v1/admin/home-features/{id}")
    public ResponseEntity<Void> removeProduct(@PathVariable Long id) {
        homeFeatureService.removeProductFromSection(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/admin/home-features/{id}/order")
    public ResponseEntity<Void> updateOrder(@PathVariable Long id, @RequestParam Integer order) {
        homeFeatureService.updateDisplayOrder(id, order);
        return ResponseEntity.ok().build();
    }

    // --- PUBLIC ENDPOINTS ---
    @GetMapping("/api/v1/public/home-features")
    public ResponseEntity<List<HomeFeatureProductDTO>> getPublicFeatures(
            @RequestParam(defaultValue = "TODAYS_SUGGESTION") String sectionType) {
        return ResponseEntity.ok(homeFeatureService.getPublicSection(sectionType));
    }
}
