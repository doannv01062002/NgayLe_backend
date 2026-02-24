package org.example.backend.repository;

import org.example.backend.model.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByPositionAndIsActiveTrueOrderByDisplayOrderAsc(Banner.BannerPosition position);
}
