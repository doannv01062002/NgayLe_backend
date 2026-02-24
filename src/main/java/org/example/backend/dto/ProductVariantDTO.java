package org.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVariantDTO {
    private Long variantId;
    private String sku;
    private String name;
    private String option1Name;
    private String option1Value;
    private String option2Name;
    private String option2Value;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private String imageUrl;
}
