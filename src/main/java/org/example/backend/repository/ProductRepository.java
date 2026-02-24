package org.example.backend.repository;

import org.example.backend.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByShop_ShopId(Long shopId, Pageable pageable);

    Page<Product> findByCategory_CategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByCategory_CategoryIdIn(List<Long> categoryIds, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isHolidaySuggestion = true AND p.status = 'ACTIVE'")
    List<Product> findHolidaySuggestions();

    @Query("SELECT p FROM Product p WHERE " +
            "(:recipient IS NULL OR p.targetAudience LIKE CONCAT('%', :recipient, '%')) AND " +
            "(:occasion IS NULL OR p.giftOccasion LIKE CONCAT('%', :occasion, '%')) AND " +
            "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.basePrice <= :maxPrice) AND " +
            "p.status = 'ACTIVE'")
    Page<Product> findGiftSuggestions(@org.springframework.data.repository.query.Param("recipient") String recipient,
            @org.springframework.data.repository.query.Param("occasion") String occasion,
            @org.springframework.data.repository.query.Param("minPrice") java.math.BigDecimal minPrice,
            @org.springframework.data.repository.query.Param("maxPrice") java.math.BigDecimal maxPrice,
            Pageable pageable);
}
