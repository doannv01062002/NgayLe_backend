package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AffiliateOverviewDTO {
    private long totalPartners;
    private long pendingPartners;
    private BigDecimal totalAffiliateRevenue;
    private BigDecimal pendingCommission;
}
