package org.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal promotionalPrice;
    private BigDecimal originalPrice;
    private Boolean isHolidaySuggestion;
    private BigDecimal rating;
    private Integer reviewCount;
    private List<ProductVariantDTO> variants;
    private List<String> imageUrls;
    private String categoryName;
    private String categorySlug;
    private String shopName;
    private Long shopId;
    private String shopLogoUrl;
    private String status;
    private Integer soldCount;
    private java.time.LocalDateTime createdAt;
    private String targetAudience;
    private String giftOccasion;
    private String brand;
    private String origin;
    private String sku;
    private String videoUrl;
    private Double weight;
    private Double height;
    private Double width;
    private Double length;
    private String metaTitle;
    private String metaDescription;
}
