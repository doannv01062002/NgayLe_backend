package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherStatsDTO {
    private long runningVouchers;
    private long totalUsage;
    private long expiringSoon; // in 7 days
    private String budgetUsed; // Mock calculation for now or sum of discountValue * usageCount
}
