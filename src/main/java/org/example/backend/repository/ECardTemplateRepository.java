package org.example.backend.repository;

import org.example.backend.model.entity.ECardTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ECardTemplateRepository extends JpaRepository<ECardTemplate, Long> {

        @Query("SELECT t FROM ECardTemplate t WHERE " +
                        "(:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:category IS NULL OR t.category = :category) " +
                        "AND (:status IS NULL " +
                        "   OR (:status = 'active' AND t.isActive = true) " +
                        "   OR (:status = 'inactive' AND t.isActive = false)" +
                        ")")
        Page<ECardTemplate> searchTemplates(@Param("keyword") String keyword,
                        @Param("category") String category,
                        @Param("status") String status,
                        Pageable pageable);

        @Query("SELECT COUNT(t) FROM ECardTemplate t")
        long countTotalTemplates();

        @Query("SELECT COALESCE(SUM(t.usageCount), 0) FROM ECardTemplate t")
        long countTotalUsage();
}
