package org.example.backend.repository;

import org.example.backend.model.entity.AffiliatePartner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffiliatePartnerRepository extends JpaRepository<AffiliatePartner, Long> {
    Optional<AffiliatePartner> findByUserUserId(Long userId);

    Page<AffiliatePartner> findByStatus(AffiliatePartner.PartnerStatus status, Pageable pageable);

    long countByStatus(AffiliatePartner.PartnerStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(a.totalRevenue) FROM AffiliatePartner a")
    java.math.BigDecimal sumTotalRevenue();

    @org.springframework.data.jpa.repository.Query("SELECT SUM(a.totalCommission) FROM AffiliatePartner a")
    java.math.BigDecimal sumTotalCommission();

    @org.springframework.data.jpa.repository.Query("SELECT a FROM AffiliatePartner a WHERE (:status IS NULL OR a.status = :status) AND (:search IS NULL OR LOWER(a.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.user.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<AffiliatePartner> searchPartners(
            @org.springframework.data.repository.query.Param("status") AffiliatePartner.PartnerStatus status,
            @org.springframework.data.repository.query.Param("search") String search, Pageable pageable);
}
