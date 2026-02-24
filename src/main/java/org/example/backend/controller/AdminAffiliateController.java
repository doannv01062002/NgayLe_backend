package org.example.backend.controller;

import org.example.backend.dto.AffiliatePartnerDTO;
import org.example.backend.service.AdminAffiliateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/affiliate")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAffiliateController {

    @Autowired
    private AdminAffiliateService adminAffiliateService;

    @GetMapping
    public ResponseEntity<Page<AffiliatePartnerDTO>> getPartners(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminAffiliateService.getPartners(status, search, pageable));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approvePartner(@PathVariable Long id) {
        adminAffiliateService.approvePartner(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectPartner(@PathVariable Long id) {
        adminAffiliateService.rejectPartner(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<AffiliatePartnerDTO> createPartner(
            @RequestBody org.example.backend.dto.CreatePartnerRequest request) {
        return ResponseEntity.ok(adminAffiliateService.createPartner(request.getEmail(), request.getSource()));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<org.example.backend.dto.CommissionHistoryDTO>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminAffiliateService.getCommissionHistory(pageable));
    }

    @GetMapping("/overview")
    public ResponseEntity<org.example.backend.dto.AffiliateOverviewDTO> getOverview() {
        return ResponseEntity.ok(adminAffiliateService.getOverview());
    }
}
