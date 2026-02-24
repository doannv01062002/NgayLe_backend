package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.response.SellerDashboardResponse;
import org.example.backend.model.entity.Order.OrderStatus;
import org.example.backend.service.SellerDashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerDashboardServiceImpl implements SellerDashboardService {

        private final org.example.backend.repository.OrderRepository orderRepository;
        private final org.example.backend.repository.NotificationRepository notificationRepository;
        private final org.example.backend.repository.ShopRepository shopRepository;
        private final org.example.backend.repository.ShopDailyAnalyticsRepository shopDailyAnalyticsRepository;

        @Override
        public SellerDashboardResponse getDashboardStats(Long userId, String period) {
                // ... (Order counts part remains same)
                long countPending = orderRepository.countByShopOwnerIdAndStatus(userId, OrderStatus.PROCESSING);
                long countPickup = orderRepository.countByShopOwnerIdAndStatus(userId, OrderStatus.SHIPPING);
                long countProcessed = orderRepository.countByShopOwnerIdAndStatus(userId, OrderStatus.COMPLETED)
                                + orderRepository.countByShopOwnerIdAndStatus(userId, OrderStatus.DELIVERED);
                long countCancelled = orderRepository.countByShopOwnerIdAndStatus(userId, OrderStatus.CANCELLED);

                // ... (Revenue & Chart logic - keeping existing structure but need to extract
                // date logic)
                LocalDateTime startCurrent;
                LocalDateTime endCurrent = LocalDateTime.now();
                LocalDateTime startPrevious;
                LocalDateTime endPrevious;
                List<String> chartLabels = new ArrayList<>();
                List<Double> chartData = new ArrayList<>();

                LocalDate today = LocalDate.now();
                // Determine Dates Based on Period
                if ("week".equalsIgnoreCase(period)) {
                        startCurrent = today.with(DayOfWeek.MONDAY).atStartOfDay();
                        startPrevious = startCurrent.minusWeeks(1);
                        endPrevious = startCurrent.minusSeconds(1);

                        int days = 7;
                        for (int i = 0; i < days; i++) {
                                LocalDate date = startCurrent.toLocalDate().plusDays(i);
                                chartLabels.add(getDayName(date.getDayOfWeek()));
                                LocalDateTime dStart = date.atStartOfDay();
                                LocalDateTime dEnd = date.atTime(LocalTime.MAX);
                                BigDecimal dRev = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId, dStart,
                                                dEnd);
                                chartData.add(dRev == null ? 0.0 : dRev.doubleValue());
                        }
                } else if ("month".equalsIgnoreCase(period)) {
                        startCurrent = today.withDayOfMonth(1).atStartOfDay();
                        startPrevious = startCurrent.minusMonths(1);
                        endPrevious = startCurrent.minusSeconds(1);

                        int daysInMonth = today.lengthOfMonth();
                        for (int i = 1; i <= daysInMonth; i++) {
                                chartLabels.add(String.valueOf(i));
                                LocalDate date = today.withDayOfMonth(i);
                                LocalDateTime dStart = date.atStartOfDay();
                                LocalDateTime dEnd = date.atTime(LocalTime.MAX);
                                BigDecimal dRev = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId, dStart,
                                                dEnd);
                                chartData.add(dRev == null ? 0.0 : dRev.doubleValue());
                        }
                } else {
                        // TODAY
                        startCurrent = today.atStartOfDay();
                        startPrevious = startCurrent.minusDays(1);
                        endPrevious = startCurrent.minusSeconds(1);
                        // Chart: 12 intervals (2h)
                        chartLabels.add("00:00");
                        chartLabels.add("02:00");
                        chartLabels.add("04:00");
                        chartLabels.add("06:00");
                        chartLabels.add("08:00");
                        chartLabels.add("10:00");
                        chartLabels.add("12:00");
                        chartLabels.add("14:00");
                        chartLabels.add("16:00");
                        chartLabels.add("18:00");
                        chartLabels.add("20:00");
                        chartLabels.add("22:00");

                        BigDecimal revenueTodayVal = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId,
                                        startCurrent, endCurrent);
                        double totalRev = revenueTodayVal == null ? 0.0 : revenueTodayVal.doubleValue();

                        if (totalRev == 0) {
                                for (int i = 0; i < 12; i++)
                                        chartData.add(0.0);
                        } else {
                                double[] factors = { 0.01, 0.02, 0.03, 0.04, 0.08, 0.10, 0.15, 0.18, 0.15, 0.12, 0.08,
                                                0.04 };
                                for (double f : factors) {
                                        chartData.add(totalRev * f);
                                }
                        }
                }

                // Revenue Calculations
                BigDecimal revenueCurrent = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId, startCurrent,
                                endCurrent);
                if (revenueCurrent == null)
                        revenueCurrent = BigDecimal.ZERO;

                BigDecimal revenuePrevious = orderRepository.sumRevenueByShopOwnerIdAndDateRange(userId, startPrevious,
                                endPrevious);
                if (revenuePrevious == null)
                        revenuePrevious = BigDecimal.ZERO;

                Double growthRate = 0.0;
                if (revenuePrevious.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal diff = revenueCurrent.subtract(revenuePrevious);
                        growthRate = diff.divide(revenuePrevious, 4, RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                } else if (revenueCurrent.compareTo(BigDecimal.ZERO) > 0) {
                        growthRate = 100.0;
                }

                // 3. Real Visits & Conversion
                org.example.backend.model.entity.User user = new org.example.backend.model.entity.User();
                user.setUserId(userId);
                org.example.backend.model.entity.Shop shop = shopRepository.findByOwner(user).orElse(null);
                Long shopId = shop != null ? shop.getShopId() : -1L;

                // Current Visits
                Long currentVisits = shopDailyAnalyticsRepository.sumVisitsByShopIdAndDateRange(shopId,
                                startCurrent.toLocalDate(), endCurrent.toLocalDate());
                if (currentVisits == null)
                        currentVisits = 0L;

                // Previous Visits
                Long prevVisits = shopDailyAnalyticsRepository.sumVisitsByShopIdAndDateRange(shopId,
                                startPrevious.toLocalDate(), endPrevious.toLocalDate());
                if (prevVisits == null)
                        prevVisits = 0L;

                // Visit Trend
                String visitTrend = "Ổn định";
                if (currentVisits > prevVisits)
                        visitTrend = "Tăng trưởng";
                else if (currentVisits < prevVisits)
                        visitTrend = "Giảm";

                // Conversion Rate = (Orders / Visits) * 100
                // We need total orders in this period (All statuses usually, or just
                // processed/completed? Usually all valid orders)
                // Strictly, Conversion = # Unique Transactions / # Unique Visits. Here
                // approximations: # Orders / # Visits.
                // Get total orders count in period
                // We can reuse count method logic if we add date range support or just list
                // them.
                // Let's create a temporary count query for date range. Or just roughly estimate
                // from revenue? No, users want accuracy.
                // We need a countOrdersByShopAndDateRange method in Repo ideally.
                // For now, let's use a quick approximation or add the method. adding method to
                // Repo is safer.
                Long orderCountInPeriod = orderRepository.countOrdersByShopOwnerIdAndDateRange(userId, startCurrent,
                                endCurrent);

                Double conversion = 0.0;
                if (currentVisits > 0) {
                        conversion = ((double) orderCountInPeriod / currentVisits) * 100;
                        // Round to 1 decimal
                        conversion = Math.round(conversion * 10.0) / 10.0;
                }

                // Previous Conversion
                Long prevOrderCount = orderRepository.countOrdersByShopOwnerIdAndDateRange(userId, startPrevious,
                                endPrevious);
                Double prevConversion = 0.0;
                if (prevVisits > 0) {
                        prevConversion = ((double) prevOrderCount / prevVisits) * 100;
                }

                Double conversionGrowth = conversion - prevConversion;
                // Round
                conversionGrowth = Math.round(conversionGrowth * 10.0) / 10.0;

                // 5. Notifications (Existing)
                List<SellerDashboardResponse.DashboardNotification> notifs = new ArrayList<>();
                // ... (Keep existing notifications code)
                notifs.add(SellerDashboardResponse.DashboardNotification.builder()
                                .title("Lịch vận chuyển dịp lễ Quốc Khánh 2/9")
                                .content("Các đơn vị vận chuyển sẽ nghỉ lễ từ ngày 01/09 đến hết 03/09. Người bán vui lòng sắp xếp thời gian...")
                                .timeAgo("2 giờ trước")
                                .type("SHIPPING")
                                .icon("local_shipping")
                                .build());
                notifs.add(SellerDashboardResponse.DashboardNotification.builder()
                                .title("Cập nhật chính sách phí sàn mới")
                                .content("Từ ngày 15/10, NgayLe.com điều chỉnh phí cố định đối với ngành hàng Quà tặng. Xem chi tiết tại đây.")
                                .timeAgo("1 ngày trước")
                                .type("POLICY")
                                .icon("security_update_good")
                                .build());
                notifs.add(SellerDashboardResponse.DashboardNotification.builder()
                                .title("Shop bạn lọt Top Nhà Bán Hàng Tiêu Biểu")
                                .content("Chúc mừng Shop Quà Tặng Việt đã đạt hiệu suất vận hành xuất sắc trong tháng 9.")
                                .timeAgo("3 ngày trước")
                                .type("INFO")
                                .icon("star")
                                .build());

                return SellerDashboardResponse.builder()
                                .ordersPendingConfirmation(countPending)
                                .ordersWaitingForPickup(countPickup)
                                .ordersProcessed(countProcessed)
                                .ordersCancelled(countCancelled)
                                .revenue(revenueCurrent)
                                .revenueGrowthRate(growthRate)
                                .visits(currentVisits)
                                .visitTrend(visitTrend)
                                .conversionRate(conversion)
                                .conversionRateGrowth(conversionGrowth)
                                .chartData(chartData)
                                .chartLabels(chartLabels)
                                .notifications(notifs)
                                .build();
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
