package org.example.backend.controller;

import org.example.backend.dto.SupportArticleDTO;
import org.example.backend.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    @Autowired
    private SupportService supportService;

    @GetMapping
    public ResponseEntity<Page<SupportArticleDTO>> getArticles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        return ResponseEntity.ok(supportService.getPublicArticles(category, search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportArticleDTO> getArticleDetail(@PathVariable Long id) {
        return ResponseEntity.ok(supportService.getArticleDetail(id));
    }
}
