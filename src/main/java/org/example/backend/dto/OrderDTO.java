package org.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long orderId;
    private Long shopId;
    private String shopName;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private String customerName;
    private String userAvatar;
    private java.util.List<OrderItemDTO> items;
}
