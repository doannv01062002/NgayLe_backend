package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ECardStatsDTO;
import org.example.backend.dto.ECardTemplateDTO;
import org.example.backend.service.ECardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/ecard-templates")
@RequiredArgsConstructor
public class ECardController {

    private final ECardService eCardService;

    @GetMapping
    public ResponseEntity<Page<ECardTemplateDTO>> getTemplates(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 12, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(eCardService.getTemplates(keyword, category, status, pageable));
    }

    @PostMapping
    public ResponseEntity<ECardTemplateDTO> createTemplate(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam(value = "isPremium", required = false) Boolean isPremium,
            @RequestParam(value = "canvasDataJson", required = false) String canvasDataJson,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(eCardService.createTemplate(name, category, isPremium, canvasDataJson, image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ECardTemplateDTO> updateTemplate(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "isPremium", required = false) Boolean isPremium,
            @RequestParam(value = "canvasDataJson", required = false) String canvasDataJson,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(eCardService.updateTemplate(id, name, category, isPremium, canvasDataJson, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        eCardService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id) {
        eCardService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<ECardStatsDTO> getStats() {
        return ResponseEntity.ok(eCardService.getStats());
    }
}
