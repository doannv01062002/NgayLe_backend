package org.example.backend.repository;

import org.example.backend.model.entity.ShopDailyAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ShopDailyAnalyticsRepository extends JpaRepository<ShopDailyAnalytics, Long> {

    Optional<ShopDailyAnalytics> findByShop_ShopIdAndDate(Long shopId, LocalDate date);

    @Query("SELECT SUM(s.visitCount) FROM ShopDailyAnalytics s WHERE s.shop.shopId = :shopId AND s.date BETWEEN :startDate AND :endDate")
    Long sumVisitsByShopIdAndDateRange(@Param("shopId") Long shopId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
