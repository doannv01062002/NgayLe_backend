package org.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long itemId;
    private Long productId;
    private String productName;
    private Long variantId;
    private String variantName;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private Boolean selected;
    private BigDecimal subtotal;
}
