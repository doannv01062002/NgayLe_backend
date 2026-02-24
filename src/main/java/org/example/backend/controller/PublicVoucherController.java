package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.VoucherDTO;
import org.example.backend.service.VoucherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class PublicVoucherController {

    private final VoucherService voucherService;

    @GetMapping("/public")
    public ResponseEntity<Page<VoucherDTO>> getPublicVouchers(
            @RequestParam(required = false) String type,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "endDate", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(voucherService.getPublicVouchers(username, type, pageable));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Void> saveVoucher(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        voucherService.saveVoucher(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }
}
