package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ShopRegisterRequest;
import org.example.backend.dto.ShopResponse;
import org.example.backend.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final org.example.backend.service.ProductService productService;
    private final org.example.backend.service.CloudinaryService cloudinaryService;
    private final org.example.backend.service.VoucherService voucherService;

    @PostMapping("/me/image")
    public ResponseEntity<String> uploadShopImage(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam("type") String type,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String url = cloudinaryService.uploadImage(file);
            org.example.backend.dto.ShopUpdateRequest updateRequest = new org.example.backend.dto.ShopUpdateRequest();
            if ("logo".equalsIgnoreCase(type)) {
                updateRequest.setLogoUrl(url);
            } else if ("banner".equalsIgnoreCase(type)) {
                updateRequest.setBannerUrl(url);
            }
            shopService.updateShop(updateRequest, userDetails.getUsername());
            return ResponseEntity.ok(url);
        } catch (java.io.IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ShopResponse> registerShop(
            @RequestBody ShopRegisterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(shopService.registerShop(request, userDetails.getUsername()));
    }

    @GetMapping("/me")
    public ResponseEntity<ShopResponse> getCurrentShop(
            @AuthenticationPrincipal UserDetails userDetails) {
        ShopResponse shop = shopService.getCurrentShop(userDetails.getUsername());
        return ResponseEntity.ok(shop);
    }

    @PutMapping("/me")
    public ResponseEntity<ShopResponse> updateShop(
            @RequestBody org.example.backend.dto.ShopUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(shopService.updateShop(request, userDetails.getUsername()));
    }

    @GetMapping("/me/products")
    public ResponseEntity<org.springframework.data.domain.Page<org.example.backend.dto.ProductDTO>> getMyProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean outOfStock,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @org.springframework.data.web.PageableDefault(size = 20, page = 0) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByShop(userDetails.getUsername(), search, status, outOfStock,
                categoryId, minStock, maxStock, pageable));
    }

    @GetMapping("/me/products/stats")
    public ResponseEntity<java.util.Map<String, Long>> getProductStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(productService.getProductStats(userDetails.getUsername()));
    }

    @PostMapping("/me/products/bulk-delete")
    public ResponseEntity<Void> bulkDeleteProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody java.util.List<Long> productIds) {
        productService.bulkDeleteProducts(productIds, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me/products/bulk-status")
    public ResponseEntity<Void> bulkUpdateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody java.util.Map<String, Object> payload) {
        // Payload: { ids: [1,2], status: "ACTIVE" }
        java.util.List<Long> ids = ((java.util.List<?>) payload.get("ids")).stream()
                .map(id -> ((Number) id).longValue())
                .collect(java.util.stream.Collectors.toList());
        String status = (String) payload.get("status");
        productService.bulkUpdateStatus(ids, status, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> getShopById(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<org.springframework.data.domain.Page<org.example.backend.dto.ProductDTO>> getShopProducts(
            @PathVariable Long id,
            @org.springframework.data.web.PageableDefault(size = 20, page = 0) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByShopId(id, pageable));
    }

    @GetMapping("/{id}/vouchers")
    public ResponseEntity<java.util.List<org.example.backend.dto.VoucherDTO>> getShopVouchers(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.getShopActiveVouchers(id));
    }
}
