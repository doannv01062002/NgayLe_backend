package org.example.backend.controller;

import org.example.backend.dto.AdminShopDTO;
import org.example.backend.service.AdminShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shops")
@PreAuthorize("hasRole('ADMIN')")
public class AdminShopController {

    @Autowired
    private AdminShopService adminShopService;

    @GetMapping
    public ResponseEntity<Page<AdminShopDTO>> getShops(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(adminShopService.getShops(search, status, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        adminShopService.updateShopStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<org.example.backend.dto.StatsDTO> getStats() {
        return ResponseEntity.ok(adminShopService.getShopStats());
    }
}
