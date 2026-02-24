package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FlashSaleDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashSaleSessionResponse {
        private Long sessionId;
        private Long shopId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean isActive;
        private String status;
        private List<FlashSaleProductResponse> products;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashSaleProductResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal originalPrice;
        private BigDecimal flashSalePrice;
        private Integer quantity;
        private Integer soldQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSessionRequest {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProductRequest {
        private Long productId;
        private BigDecimal flashSalePrice;
        private Integer quantity;
    }
}
