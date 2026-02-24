package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ECardTemplateDTO;
import org.example.backend.service.ECardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ecard-templates")
@RequiredArgsConstructor
public class CustomerECardController {

    private final ECardService eCardService;

    @GetMapping
    public ResponseEntity<Page<ECardTemplateDTO>> getPublicTemplates(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 12, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        // Always force status to "active" for public access
        return ResponseEntity.ok(eCardService.getTemplates(keyword, category, "active", pageable));
    }

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadUserCard(
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(eCardService.uploadUserCard(file));
    }
}
