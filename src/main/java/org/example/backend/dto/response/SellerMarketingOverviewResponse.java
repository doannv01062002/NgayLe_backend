package org.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerMarketingOverviewResponse {
    private BigDecimal adRevenue;
    private Double adRevenueGrowth;

    private BigDecimal vouchersRedeemed; // Count of usages or Value? UI says "342", implying count.
    private Double vouchersRedeemedGrowth;

    private Double conversionRate;
    private Double conversionRateGrowth;

    private long visits;
    private Double visitsGrowth;

    // Chart data (Revenue form Ads/Marketing)
    private List<Double> chartData;
    private List<String> chartLabels;

    private List<UpcomingEvent> upcomingEvents;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpcomingEvent {
        private String date; // "02"
        private String month; // "Th09"
        private String title; // "Quốc Khánh 2/9"
        private String description; // "Lượng truy cập dự kiến tăng 300%..."
        private String actionLabel; // "Tạo Deal Ngay"
        private String actionUrl;
    }
}
