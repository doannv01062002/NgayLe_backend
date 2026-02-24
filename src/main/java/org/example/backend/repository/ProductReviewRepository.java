package org.example.backend.repository;

import org.example.backend.model.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
        @org.springframework.data.jpa.repository.Query("SELECT r FROM ProductReview r WHERE r.product.productId = :productId AND (r.isHidden IS NULL OR r.isHidden = false)")
        Page<ProductReview> findByProduct_ProductId(
                        @org.springframework.data.repository.query.Param("productId") Long productId,
                        Pageable pageable);

        // For calculating average rating - Only count visible reviews?
        // Usually yes, hidden reviews shouldn't count towards rating
        @org.springframework.data.jpa.repository.Query("SELECT r FROM ProductReview r WHERE r.product.productId = :productId AND (r.isHidden IS NULL OR r.isHidden = false)")
        List<ProductReview> findByProduct_ProductId(
                        @org.springframework.data.repository.query.Param("productId") Long productId);

        @org.springframework.data.jpa.repository.Query("SELECT r FROM ProductReview r WHERE " +
                        "(:keyword IS NULL OR r.product.name ILIKE %:keyword% OR r.user.fullName ILIKE %:keyword% OR r.comment ILIKE %:keyword%) "
                        +
                        "AND (:rating IS NULL OR r.rating = :rating)")
        Page<ProductReview> searchReviews(@org.springframework.data.repository.query.Param("keyword") String keyword,
                        @org.springframework.data.repository.query.Param("rating") Integer rating,
                        Pageable pageable);
}
