package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.entity.Sticker;
import org.example.backend.service.StickerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StickerController {

    private final StickerService stickerService;

    @GetMapping("/api/v1/stickers")
    public ResponseEntity<List<Sticker>> getAllStickers() {
        return ResponseEntity.ok(stickerService.getAllStickers());
    }

    @PostMapping(value = "/api/v1/admin/stickers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sticker> createSticker(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(stickerService.createSticker(name, category, image));
    }

    @DeleteMapping("/api/v1/admin/stickers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSticker(@PathVariable Long id) {
        stickerService.deleteSticker(id);
        return ResponseEntity.ok().build();
    }
}
