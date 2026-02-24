package org.example.backend.service;

import org.example.backend.dto.BannerDTO;
import org.example.backend.model.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BannerService {
    Page<BannerDTO> getBanners(String search, Banner.BannerPosition position, Boolean isActive, Pageable pageable);

    BannerDTO createBanner(BannerDTO bannerDTO);

    BannerDTO updateBanner(Long id, BannerDTO bannerDTO);

    void deleteBanner(Long id);

    BannerDTO getBanner(Long id);

    List<BannerDTO> getPublicBanners(Banner.BannerPosition position);

    BannerDTO toggleStatus(Long id);

    Map<String, Object> getStats();

    void incrementClick(Long id);

    void incrementView(Long id);
}
