package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.CreateVoucherRequest;
import org.example.backend.dto.VoucherDTO;
import org.example.backend.dto.VoucherStatsDTO;
import org.example.backend.service.VoucherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public ResponseEntity<Page<VoucherDTO>> getVouchers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(voucherService.getVouchers(keyword, type, status, date, pageable));
    }

    @PostMapping
    public ResponseEntity<VoucherDTO> createVoucher(@RequestBody CreateVoucherRequest request) {
        return ResponseEntity.ok(voucherService.createVoucher(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO> getVoucher(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.getVoucher(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO> updateVoucher(@PathVariable Long id, @RequestBody CreateVoucherRequest request) {
        return ResponseEntity.ok(voucherService.updateVoucher(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle-pause")
    public ResponseEntity<Void> togglePause(@PathVariable Long id) {
        voucherService.togglePause(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<VoucherStatsDTO> getStats() {
        return ResponseEntity.ok(voucherService.getVoucherStats());
    }
}
