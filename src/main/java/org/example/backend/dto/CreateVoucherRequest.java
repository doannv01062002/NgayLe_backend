package org.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateVoucherRequest {
    private String code;
    private String name;
    private String description;
    private String type; // REDEEM, SHIPPING, STOREFRONT
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private Integer usageLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
