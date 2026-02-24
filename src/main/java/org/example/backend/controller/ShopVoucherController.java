package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.CreateVoucherRequest;
import org.example.backend.dto.VoucherDTO;
import org.example.backend.model.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.VoucherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shops/me/vouchers")
@RequiredArgsConstructor
public class ShopVoucherController {

    private final VoucherService voucherService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<VoucherDTO>> getMyVouchers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        User user = getUser(userDetails);
        // Assuming voucherService.getVouchersByShopOwner handled filtering by user.id
        // -> shop
        // Actually existing VoucherService.getVouchers is generic. We need a specific
        // one for Shop.
        // Let's assume for now we reuse generic getVouchers but filter by shopId inside
        // service if we ask it to.
        // Or better, create a new method in VoucherService: getVouchersByShop(Long
        // shopId, ...)

        // For now, I will create the Controller and assume the Service needs an update.
        // I'll call a method that I will add to Service next step.
        return ResponseEntity.ok(voucherService.getVouchersByOwner(user.getUserId(), keyword, type, status, pageable));
    }

    @PostMapping
    public ResponseEntity<VoucherDTO> createShopVoucher(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateVoucherRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(voucherService.createShopVoucher(user.getUserId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO> getShopVoucher(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(voucherService.getShopVoucher(user.getUserId(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO> updateShopVoucher(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody CreateVoucherRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(voucherService.updateShopVoucher(user.getUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShopVoucher(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = getUser(userDetails);
        voucherService.deleteShopVoucher(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
