package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.response.SellerMarketingOverviewResponse;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.ShopDailyAnalyticsRepository;
import org.example.backend.repository.ShopRepository;
import org.example.backend.repository.VoucherRepository;
import org.example.backend.service.SellerMarketingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerMarketingServiceImpl implements SellerMarketingService {

    private final ShopRepository shopRepository;
    private final ShopDailyAnalyticsRepository shopDailyAnalyticsRepository;
    private final OrderRepository orderRepository;
    private final VoucherRepository voucherRepository;

    @Override
    public SellerMarketingOverviewResponse getMarketingOverview(Long userId, String period) {
        // Resolve Shop
        org.example.backend.model.entity.User user = new org.example.backend.model.entity.User();
        user.setUserId(userId);
        org.example.backend.model.entity.Shop shop = shopRepository.findByOwner(user).orElse(null);
        Long shopId = shop != null ? shop.getShopId() : -1L;

        // Determine Date Range
        LocalDate today = LocalDate.now();
        LocalDateTime startCurrent, endCurrent = LocalDateTime.now(), startPrevious, endPrevious;

        // Logic similar to Dashboard, default to "30 days" for Marketing usually, but
        // let's support period param
        // If "period" is not passed or "30days" (default for Marketing UI), we use 30
        // days.
        if ("week".equalsIgnoreCase(period)) {
            startCurrent = today.with(DayOfWeek.MONDAY).atStartOfDay();
            startPrevious = startCurrent.minusWeeks(1);
            endPrevious = startCurrent.minusSeconds(1);
        } else if ("month".equalsIgnoreCase(period)) {
            startCurrent = today.withDayOfMonth(1).atStartOfDay();
            startPrevious = startCurrent.minusMonths(1);
            endPrevious = startCurrent.minusSeconds(1);
        } else {
            // Default 30 days
            startCurrent = today.minusDays(29).atStartOfDay();
            startPrevious = startCurrent.minusDays(30);
            endPrevious = startCurrent.minusSeconds(1);
        }

        // 1. Visits (Lượt truy cập)
        Long currentVisits = shopDailyAnalyticsRepository.sumVisitsByShopIdAndDateRange(shopId,
                startCurrent.toLocalDate(), endCurrent.toLocalDate());
        if (currentVisits == null)
            currentVisits = 0L;
        Long prevVisits = shopDailyAnalyticsRepository.sumVisitsByShopIdAndDateRange(shopId,
                startPrevious.toLocalDate(), endPrevious.toLocalDate());
        if (prevVisits == null)
            prevVisits = 0L;

        Double visitsGrowth = calculateGrowth(BigDecimal.valueOf(currentVisits), BigDecimal.valueOf(prevVisits));

        // 2. Conversion Rate
        Long orderCount = orderRepository.countOrdersByShopOwnerIdAndDateRange(userId, startCurrent, endCurrent);
        Long prevOrderCount = orderRepository.countOrdersByShopOwnerIdAndDateRange(userId, startPrevious, endPrevious);

        Double conversion = 0.0;
        if (currentVisits > 0)
            conversion = ((double) orderCount / currentVisits) * 100;
        conversion = Math.round(conversion * 10.0) / 10.0;

        Double prevConversion = 0.0;
        if (prevVisits > 0)
            prevConversion = ((double) prevOrderCount / prevVisits) * 100;

        Double conversionGrowth = conversion - prevConversion;
        conversionGrowth = Math.round(conversionGrowth * 10.0) / 10.0;

        // 3. Ad Revenue (Doanh thu từ QC) - Mocked for now as we don't have Ads system
        // Let's assume 10% of total revenue is "Ad Driven" for simulation or 0 if no
        // revenue
        BigDecimal totalRevenue = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId, startCurrent, endCurrent);
        if (totalRevenue == null)
            totalRevenue = BigDecimal.ZERO;

        BigDecimal adRevenue = totalRevenue.multiply(BigDecimal.valueOf(0.1)); // Mock 10%
        adRevenue = adRevenue.setScale(0, RoundingMode.HALF_UP); // Round to integer

        BigDecimal totalRevenuePrev = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId, startPrevious,
                endPrevious);
        if (totalRevenuePrev == null)
            totalRevenuePrev = BigDecimal.ZERO;
        BigDecimal adRevenuePrev = totalRevenuePrev.multiply(BigDecimal.valueOf(0.1));

        Double adRevenueGrowth = calculateGrowth(adRevenue, adRevenuePrev);

        // 4. Vouchers Redeemed (Voucher đã dùng)
        // We only have total usage count in Voucher entity, not historical usage log by
        // date.
        // So we can only return TOTAL usage or MOCK the period usage.
        // Ideally we need a VoucherUsageHistory entity.
        // For now, let's return the Total Sum of usages for all shop vouchers as a
        // proxy,
        // or Mock a "Growth" based on that.
        Long totalVoucherUsage = voucherRepository.sumUsageByShopId(shopId);
        if (totalVoucherUsage == null)
            totalVoucherUsage = 0L;

        // Mock current period usage as 20% of total usage
        BigDecimal vouchersRedeemed = BigDecimal.valueOf(totalVoucherUsage).multiply(BigDecimal.valueOf(0.2))
                .setScale(0, RoundingMode.CEILING);
        Double vouchersRedeemedGrowth = 5.2; // Mock fixed growth

        // 5. Chart Data (Ad Efficiency - Revenue over time)
        List<String> chartLabels = new ArrayList<>();
        List<Double> chartData = new ArrayList<>();
        // Generate daily data for the period
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startCurrent.toLocalDate(),
                endCurrent.toLocalDate()) + 1;
        // Limit to 7-10 points for chart readability if 30 days, or show all
        // The design shows "T2, T3..." implying weekly view or compressed view.
        // Let's toggle based on period provided.
        if (daysBetween <= 7) {
            for (int i = 0; i < daysBetween; i++) {
                LocalDate date = startCurrent.toLocalDate().plusDays(i);
                chartLabels.add(getDayName(date.getDayOfWeek()));
                // Mock daily ad revenue
                BigDecimal dRev = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId, date.atStartOfDay(),
                        date.atTime(LocalTime.MAX));
                if (dRev == null)
                    dRev = BigDecimal.ZERO;
                chartData.add(dRev.multiply(BigDecimal.valueOf(0.1)).doubleValue());
            }
        } else {
            // For 30 days, group by 3-4 days or just show last 7 days? UI says "30 ngày
            // qua" but chart x-axis is T2-CN (Week).
            // Maybe the chart shows "Last Week" trend even if overview is 30 days?
            // Or maybe it aggregates by week?
            // Let's show last 7 days for the chart to match the T2-CN style.
            LocalDate chartStart = today.minusDays(6);
            for (int i = 0; i < 7; i++) {
                LocalDate date = chartStart.plusDays(i);
                chartLabels.add(getDayName(date.getDayOfWeek()));
                BigDecimal dRev = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId, date.atStartOfDay(),
                        date.atTime(LocalTime.MAX));
                if (dRev == null)
                    dRev = BigDecimal.ZERO;
                chartData.add(dRev.multiply(BigDecimal.valueOf(0.1)).doubleValue());
            }
        }

        // 6. Upcoming Events (Dynamic)
        List<SellerMarketingOverviewResponse.UpcomingEvent> events = org.example.backend.util.MarketingEventUtils
                .getUpcomingEvents();

        return SellerMarketingOverviewResponse.builder()
                .adRevenue(adRevenue)
                .adRevenueGrowth(adRevenueGrowth)
                .vouchersRedeemed(vouchersRedeemed)
                .vouchersRedeemedGrowth(vouchersRedeemedGrowth)
                .conversionRate(conversion)
                .conversionRateGrowth(conversionGrowth)
                .visits(currentVisits)
                .visitsGrowth(visitsGrowth)
                .chartData(chartData)
                .chartLabels(chartLabels)
                .upcomingEvents(events)
                .build();
    }

    private Double calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous).divide(previous, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private String getDayName(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return "T2";
            case TUESDAY:
                return "T3";
            case WEDNESDAY:
                return "T4";
            case THURSDAY:
                return "T5";
            case FRIDAY:
                return "T6";
            case SATURDAY:
                return "T7";
            case SUNDAY:
                return "CN";
            default:
                return "";
        }
    }
}
