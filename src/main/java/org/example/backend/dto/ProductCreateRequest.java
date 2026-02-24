package org.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductCreateRequest {
    private String name;
    private String description;
    private Long categoryId;
    private BigDecimal basePrice;
    private BigDecimal originalPrice;
    private Integer stock;
    private List<ProductVariantDTO> variants;
    private List<String> imageUrls;
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
