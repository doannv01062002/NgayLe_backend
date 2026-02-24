package org.example.backend.repository;

import org.example.backend.model.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

        @Query("SELECT COUNT(v) FROM Voucher v WHERE v.shopId = :shopId")
        long countByShopId(@Param("shopId") Long shopId);

        @Query("SELECT COALESCE(SUM(v.usageCount), 0) FROM Voucher v WHERE v.shopId = :shopId")
        Long sumUsageByShopId(@Param("shopId") Long shopId);

        boolean existsByCode(String code);

        @Query("SELECT v FROM Voucher v WHERE " +
                        "(:keyword IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
                        +
                        "(:type IS NULL OR :type = '' OR v.type = :type) AND " +
                        "(:status IS NULL OR :status = '' OR " +
                        "  (:status = 'RUNNING' AND v.isActive = true AND v.startDate <= CURRENT_TIMESTAMP AND v.endDate >= CURRENT_TIMESTAMP) OR "
                        +
                        "  (:status = 'UPCOMING' AND v.isActive = true AND v.startDate > CURRENT_TIMESTAMP) OR " +
                        "  (:status = 'EXPIRED' AND v.endDate < CURRENT_TIMESTAMP) OR " +
                        "  (:status = 'OFFLINE' AND v.isActive = false)" +
                        ") AND " +
                        "(:date IS NULL OR v.endDate >= :date)")
        Page<Voucher> searchVouchers(@Param("keyword") String keyword,
                        @Param("type") String type,
                        @Param("status") String status,
                        @Param("date") LocalDate date,
                        Pageable pageable);

        @Query("SELECT COUNT(v) FROM Voucher v WHERE v.isActive = true AND v.startDate <= CURRENT_TIMESTAMP AND v.endDate >= CURRENT_TIMESTAMP")
        long countRunningVouchers();

        @Query("SELECT COALESCE(SUM(v.usageCount), 0) FROM Voucher v")
        long countTotalUsage();

        @Query("SELECT COUNT(v) FROM Voucher v WHERE v.endDate <= :date AND v.endDate >= CURRENT_TIMESTAMP")
        long countExpiringSoon(@Param("date") LocalDateTime date);

        @Query("SELECT v FROM Voucher v WHERE " +
                        "v.shopId = :shopId AND " +
                        "(:keyword IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
                        +
                        "(:type IS NULL OR :type = '' OR v.type = :type) AND " +
                        "(:status IS NULL OR :status = '' OR " +
                        "  (:status = 'RUNNING' AND v.isActive = true AND v.startDate <= CURRENT_TIMESTAMP AND v.endDate >= CURRENT_TIMESTAMP) OR "
                        +
                        "  (:status = 'UPCOMING' AND v.isActive = true AND v.startDate > CURRENT_TIMESTAMP) OR " +
                        "  (:status = 'EXPIRED' AND v.endDate < CURRENT_TIMESTAMP) OR " +
                        "  (:status = 'OFFLINE' AND v.isActive = false)" +
                        ")")
        Page<Voucher> searchShopVouchers(@Param("shopId") Long shopId,
                        @Param("keyword") String keyword,
                        @Param("type") String type,
                        @Param("status") String status,
                        Pageable pageable);

        @Query("SELECT v FROM Voucher v WHERE v.shopId = :shopId AND v.isActive = true AND v.startDate <= :now AND v.endDate >= :now AND v.usageCount < v.usageLimit")
        java.util.List<Voucher> findActiveVouchersByShopId(@Param("shopId") Long shopId,
                        @Param("now") LocalDateTime now);
}
