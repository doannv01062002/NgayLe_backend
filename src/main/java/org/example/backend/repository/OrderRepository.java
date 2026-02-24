package org.example.backend.repository;

import org.example.backend.model.entity.Order;
import org.example.backend.model.entity.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>,
                org.springframework.data.jpa.repository.JpaSpecificationExecutor<Order> {
        List<Order> findByUser_UserId(Long userId);

        List<Order> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

        List<Order> findAllByOrderByCreatedAtDesc();

        @Query("SELECT o FROM Order o WHERE o.shop.owner.userId = :userId")
        Page<Order> findByShopOwnerId(@Param("userId") Long userId, Pageable pageable);

        @Query("SELECT o FROM Order o WHERE o.shop.owner.userId = :userId AND o.status = :status")
        Page<Order> findByShopOwnerIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status,
                        Pageable pageable);

        // Add more complex search if needed (search by Code/BuyerName)
        @Query("SELECT o FROM Order o WHERE o.shop.owner.userId = :userId " +
                        "AND (:status IS NULL OR o.status = :status) " +
                        "AND (:keyword IS NULL OR (LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(o.trackingCode) LIKE LOWER(CONCAT('%', :keyword, '%')))) "
                        +
                        "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
                        "AND (:endDate IS NULL OR o.createdAt <= :endDate)")
        Page<Order> findSellerOrders(@Param("userId") Long userId,
                        @Param("status") OrderStatus status,
                        @Param("keyword") String keyword,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        @Query("SELECT COUNT(o) FROM Order o WHERE o.shop.owner.userId = :userId AND o.status = :status")
        long countByShopOwnerIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);

        @Query("SELECT COUNT(o) FROM Order o WHERE o.shop.owner.userId = :userId AND o.createdAt BETWEEN :startDate AND :endDate AND o.status NOT IN ('CANCELLED', 'RETURNED')")
        long countOrdersByShopOwnerIdAndDateRange(@Param("userId") Long userId,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.shop.owner.userId = :userId AND o.createdAt BETWEEN :startDate AND :endDate AND o.status NOT IN ('CANCELLED', 'RETURNED')")
        java.math.BigDecimal sumRevenueByShopOwnerIdAndDateRange(@Param("userId") Long userId,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        // Global stats for Admin
        @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status NOT IN ('CANCELLED', 'RETURNED')")
        java.math.BigDecimal sumTotalRevenue();

        @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status NOT IN ('CANCELLED', 'RETURNED')")
        java.math.BigDecimal sumTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
