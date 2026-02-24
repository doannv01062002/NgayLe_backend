package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeFeatureProductDTO {
    private Long id; // ID of the feature entry
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer displayOrder;
}
