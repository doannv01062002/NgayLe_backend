package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.BannerDTO;
import org.example.backend.model.entity.Banner;
import org.example.backend.service.BannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/banners")
@RequiredArgsConstructor
public class PublicBannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<List<BannerDTO>> getBanners(
            @RequestParam Banner.BannerPosition position) {
        return ResponseEntity.ok(bannerService.getPublicBanners(position));
    }

    @PostMapping("/{id}/click")
    public ResponseEntity<Void> trackClick(@PathVariable Long id) {
        bannerService.incrementClick(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> trackView(@PathVariable Long id) {
        bannerService.incrementView(id);
        return ResponseEntity.ok().build();
    }
}
