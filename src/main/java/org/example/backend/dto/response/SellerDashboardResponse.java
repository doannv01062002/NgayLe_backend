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
public class SellerDashboardResponse {
    private long ordersPendingConfirmation;
    private long ordersWaitingForPickup;
    private long ordersProcessed;
    private long ordersCancelled;

    private BigDecimal revenue; // Current period revenue
    private Double revenueGrowthRate; // Percentage (e.g., 12.5)

    private long visits; // Current period visits
    private String visitTrend; // "Stable", "Increased", etc.

    private Double conversionRate;
    private Double conversionRateGrowth;

    // Chart data
    private List<Double> chartData;
    private List<String> chartLabels;

    private List<DashboardNotification> notifications;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DashboardNotification {
        private String title;
        private String content;
        private String timeAgo;
        private String type; // SHIPPING, POLICY, INFO
        private String icon; // Material symbol name
    }
}
