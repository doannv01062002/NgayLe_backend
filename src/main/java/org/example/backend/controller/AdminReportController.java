package org.example.backend.controller;

import org.example.backend.dto.ReportDTO;
import org.example.backend.service.AdminReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    @Autowired
    private AdminReportService adminReportService;

    @GetMapping
    public ResponseEntity<Page<ReportDTO>> getReports(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(adminReportService.getReports(targetType, status, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        adminReportService.updateReportStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<org.example.backend.dto.StatsDTO> getStats() {
        return ResponseEntity.ok(adminReportService.getStats());
    }
}
