package org.example.backend.controller;

import org.example.backend.dto.SupportArticleDTO;
import org.example.backend.service.AdminSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/support")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupportController {

    @Autowired
    private AdminSupportService adminSupportService;

    @GetMapping
    public ResponseEntity<Page<SupportArticleDTO>> getArticles(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return ResponseEntity.ok(adminSupportService.getArticles(status, category, search, pageable));
    }

    @PostMapping
    public ResponseEntity<SupportArticleDTO> createArticle(@RequestBody SupportArticleDTO dto) {
        return ResponseEntity.ok(adminSupportService.createArticle(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupportArticleDTO> updateArticle(@PathVariable Long id, @RequestBody SupportArticleDTO dto) {
        return ResponseEntity.ok(adminSupportService.updateArticle(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        adminSupportService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/overview")
    public ResponseEntity<org.example.backend.dto.SupportOverviewDTO> getOverview() {
        return ResponseEntity.ok(adminSupportService.getOverview());
    }
}
