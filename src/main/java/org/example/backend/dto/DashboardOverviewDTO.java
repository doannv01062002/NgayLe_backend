package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardOverviewDTO {
    private BigDecimal totalRevenue;
    private long totalVisits;
    private long totalOrders;
    private long newCustomers;

    private double revenueGrowth;
    private double visitsGrowth;
    private double ordersGrowth;
    private double customersGrowth;

    private List<OrderDTO> recentOrders;

    // Simple chart data: just a list of numbers for the last 7 days or similar
    private List<BigDecimal> revenueChartData;
}
