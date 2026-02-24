package org.example.backend.repository.specification;

import org.example.backend.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> filterBy(String search, List<Long> categoryIds, BigDecimal minPrice,
            BigDecimal maxPrice, Double rating, Long shopId, Product.ProductStatus status, Boolean outOfStock,
            Integer minStock, Integer maxStock) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();
            query.distinct(true); // Avoid duplicates from joins

            // Search by name
            if (search != null && !search.isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                                "%" + search.toLowerCase() + "%"));
            }

            // Category filter
            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, root.get("category").get("categoryId").in(categoryIds));
            }

            // Price range filter
            if (minPrice != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("basePrice"), minPrice));
            }
            if (maxPrice != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.lessThanOrEqualTo(root.get("basePrice"), maxPrice));
            }

            // Rating filter
            if (rating != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), BigDecimal.valueOf(rating)));
            }

            // Shop filter
            if (shopId != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("shop").get("shopId"), shopId));
            }

            // Status filter
            if (status != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("status"), status));
            }

            // Stock Link (Join variants once if needed)
            if (Boolean.TRUE.equals(outOfStock) || minStock != null || maxStock != null) {
                var variantsJoin = root.join("variants");

                // Out of Stock filter
                if (Boolean.TRUE.equals(outOfStock)) {
                    predicates = criteriaBuilder.and(predicates,
                            criteriaBuilder.lessThanOrEqualTo(variantsJoin.get("stockQuantity"), 0));
                }

                if (minStock != null) {
                    predicates = criteriaBuilder.and(predicates,
                            criteriaBuilder.greaterThanOrEqualTo(variantsJoin.get("stockQuantity"), minStock));
                }

                if (maxStock != null) {
                    predicates = criteriaBuilder.and(predicates,
                            criteriaBuilder.lessThanOrEqualTo(variantsJoin.get("stockQuantity"), maxStock));
                }
            }

            return predicates;
        };
    }
}
