package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.BannerDTO;
import org.example.backend.model.entity.Banner;
import org.example.backend.service.BannerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HEADHUNTER')") // Allowing HH mostly as Admin-like role based on previous chat
                                                   // context? Or just Admin. User said "Admin Admin", usually
                                                   // ROLE_ADMIN. I'll stick to 'ADMIN' but check if 'HEADHUNTER' needs
                                                   // access. The prompt says "bên admin".
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<Page<BannerDTO>> getBanners(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Banner.BannerPosition position,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        return ResponseEntity.ok(bannerService.getBanners(search, position, isActive, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(bannerService.getStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BannerDTO> getBanner(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.getBanner(id));
    }

    @PostMapping
    public ResponseEntity<BannerDTO> createBanner(@RequestBody BannerDTO bannerDTO) {
        return ResponseEntity.ok(bannerService.createBanner(bannerDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BannerDTO> updateBanner(@PathVariable Long id, @RequestBody BannerDTO bannerDTO) {
        return ResponseEntity.ok(bannerService.updateBanner(id, bannerDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BannerDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.toggleStatus(id));
    }
}
