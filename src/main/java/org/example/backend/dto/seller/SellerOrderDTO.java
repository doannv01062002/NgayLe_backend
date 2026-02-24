package org.example.backend.dto.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerOrderDTO {
    private Long id;
    private String orderId; // Display ID e.g., 240125ABC123
    private String buyerName;
    private String status; // Localized or Enum string
    private String statusKey; // Enum string for logic
    private List<SellerOrderItemDTO> items;
    private String total; // Formatted string
    private String paymentMethod;
    private String shippingMethod;
    private String deadline;
    private String note;
    private String shippingAddress;
    private String createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SellerOrderItemDTO {
        private String name;
        private String variant;
        private Integer quantity;
        private String price; // Formatted
        private String image;
    }
}
