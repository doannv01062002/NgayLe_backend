package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.FlashSaleDTO;
import org.example.backend.service.FlashSaleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    // --- ADMIN ENDPOINTS ---
    @GetMapping("/api/v1/admin/flash-sales")
    public ResponseEntity<Page<FlashSaleDTO.FlashSaleSessionResponse>> getAllSessions(
            @PageableDefault(sort = "startTime", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(flashSaleService.getAllSessions(pageable));
    }

    @PostMapping("/api/v1/admin/flash-sales")
    public ResponseEntity<FlashSaleDTO.FlashSaleSessionResponse> createSession(
            @RequestBody FlashSaleDTO.CreateSessionRequest request) {
        return ResponseEntity.ok(flashSaleService.createSession(request));
    }

    @GetMapping("/api/v1/admin/flash-sales/{id}")
    public ResponseEntity<FlashSaleDTO.FlashSaleSessionResponse> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(flashSaleService.getSession(id));
    }

    @DeleteMapping("/api/v1/admin/flash-sales/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        flashSaleService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/admin/flash-sales/{id}/toggle")
    public ResponseEntity<Void> toggleSession(@PathVariable Long id) {
        flashSaleService.toggleSessionStatus(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/admin/flash-sales/{id}/products")
    public ResponseEntity<Void> addProducts(@PathVariable Long id,
            @RequestBody List<FlashSaleDTO.AddProductRequest> products) {
        flashSaleService.addProductsToSession(id, products);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/v1/admin/flash-sales/products/{flashSaleProductId}")
    public ResponseEntity<Void> removeProduct(@PathVariable Long flashSaleProductId) {
        flashSaleService.removeProductFromSession(flashSaleProductId);
        return ResponseEntity.noContent().build();
    }

    // --- SELLER ENDPOINTS ---
    @GetMapping("/api/v1/seller/shops/{shopId}/flash-sales")
    public ResponseEntity<Page<FlashSaleDTO.FlashSaleSessionResponse>> getShopSessions(
            @PathVariable Long shopId,
            @PageableDefault(sort = "startTime", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(flashSaleService.getShopSessions(shopId, pageable));
    }

    @PostMapping("/api/v1/seller/shops/{shopId}/flash-sales")
    public ResponseEntity<FlashSaleDTO.FlashSaleSessionResponse> createShopSession(
            @PathVariable Long shopId,
            @RequestBody FlashSaleDTO.CreateSessionRequest request) {
        return ResponseEntity.ok(flashSaleService.createShopSession(shopId, request));
    }

    @DeleteMapping("/api/v1/seller/shops/{shopId}/flash-sales/{sessionId}")
    public ResponseEntity<Void> deleteShopSession(
            @PathVariable Long shopId,
            @PathVariable Long sessionId) {
        flashSaleService.deleteShopSession(shopId, sessionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/seller/shops/{shopId}/flash-sales/{sessionId}/products")
    public ResponseEntity<Void> addShopProducts(
            @PathVariable Long shopId,
            @PathVariable Long sessionId,
            @RequestBody List<FlashSaleDTO.AddProductRequest> products) {
        flashSaleService.addProductsToShopSession(shopId, sessionId, products);
        return ResponseEntity.ok().build();
    }

    // --- PUBLIC ENDPOINTS ---
    @GetMapping("/api/v1/public/flash-sales/current")
    public ResponseEntity<FlashSaleDTO.FlashSaleSessionResponse> getCurrentFlashSale() {
        return ResponseEntity.ok(flashSaleService.getCurrentFlashSale());
    }

    @GetMapping("/api/v1/public/shops/{shopId}/flash-sales/current")
    public ResponseEntity<FlashSaleDTO.FlashSaleSessionResponse> getShopCurrentFlashSale(@PathVariable Long shopId) {
        return ResponseEntity.ok(flashSaleService.getShopActiveFlashSale(shopId));
    }
}
