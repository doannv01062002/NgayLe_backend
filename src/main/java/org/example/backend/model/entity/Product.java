package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@lombok.ToString(exclude = { "variants", "images", "reviews", "shop", "category" })
@lombok.EqualsAndHashCode(exclude = { "variants", "images", "reviews", "shop", "category" })
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "promotional_price")
    private BigDecimal promotionalPrice;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "is_holiday_suggestion")
    private Boolean isHolidaySuggestion = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "rating")
    private java.math.BigDecimal rating = java.math.BigDecimal.ZERO;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "sold_count")
    private Integer soldCount = 0;

    @Column(name = "target_audience")
    private String targetAudience; // Comma separated: LOVER, PARENTS, ...

    @Column(name = "gift_occasion")
    private String giftOccasion; // Comma separated: BIRTHDAY, TET, ...

    @Column(name = "brand")
    private String brand;

    @Column(name = "origin")
    private String origin;

    // Single product management
    @Column(name = "sku")
    private String sku;

    // Media
    @Column(name = "video_url", columnDefinition = "LONGTEXT")
    private String videoUrl;

    // Shipping
    @Column(name = "weight")
    private Double weight; // grams

    @Column(name = "height")
    private Double height; // cm

    @Column(name = "width")
    private Double width; // cm

    @Column(name = "length")
    private Double length; // cm

    // SEO
    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "LONGTEXT")
    private String metaDescription;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ProductVariant> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ProductImage> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private java.util.List<ProductReview> reviews;

    public enum ProductStatus {
        DRAFT, PENDING_REVIEW, ACTIVE, INACTIVE, BANNED
    }
}
