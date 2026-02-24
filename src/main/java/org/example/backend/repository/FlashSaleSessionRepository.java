package org.example.backend.repository;

import org.example.backend.model.entity.FlashSaleSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleSessionRepository extends JpaRepository<FlashSaleSession, Long> {

    // Platform Overlap: (shop is null) AND overlaps
    @Query("SELECT s FROM FlashSaleSession s WHERE s.shop IS NULL AND " +
            "s.startTime < :end AND s.endTime > :start")
    List<FlashSaleSession> findOverlappingSessions(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM FlashSaleSession s WHERE s.shop.shopId = :shopId AND " +
            "s.startTime < :end AND s.endTime > :start")
    List<FlashSaleSession> findOverlappingShopSessions(Long shopId, LocalDateTime start, LocalDateTime end);

    // Platform Active Session
    @Query("SELECT s FROM FlashSaleSession s WHERE s.shop IS NULL AND s.isActive = true AND :now BETWEEN s.startTime AND s.endTime")
    Optional<FlashSaleSession> findCurrentActiveSession(LocalDateTime now);

    // Shop Active Session
    @Query("SELECT s FROM FlashSaleSession s WHERE s.shop.shopId = :shopId AND s.isActive = true AND :now BETWEEN s.startTime AND s.endTime")
    Optional<FlashSaleSession> findShopActiveSession(Long shopId, LocalDateTime now);

    @Query("SELECT s FROM FlashSaleSession s WHERE s.isActive = true AND s.startTime > :now ORDER BY s.startTime ASC")
    List<FlashSaleSession> findUpcomingSessions(LocalDateTime now);

    Page<FlashSaleSession> findAllByOrderByStartTimeDesc(Pageable pageable);

    Page<FlashSaleSession> findAllByShop_ShopIdOrderByStartTimeDesc(Long shopId, Pageable pageable);
}
