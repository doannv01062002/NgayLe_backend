package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String type;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private Integer usageLimit;
    private Integer usageCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private String status; // Derived field: RUNNING, UPCOMING, ENDED, PAUSED
    private String conditionDescription; // Derived
    private String colorClass; // Derived for UI (optional, but convenient)

    @com.fasterxml.jackson.annotation.JsonProperty("isSaved")
    private Boolean isSaved; // Whether the current user has saved this voucher
}
