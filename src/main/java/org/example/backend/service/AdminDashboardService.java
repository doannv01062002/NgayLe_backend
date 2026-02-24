package org.example.backend.service;

import org.example.backend.dto.DashboardOverviewDTO;
import org.example.backend.dto.OrderDTO;
import org.example.backend.model.entity.Order;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.repository.VisitLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminDashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VisitLogRepository visitLogRepository;

    public DashboardOverviewDTO getOverview() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusSeconds(1);

        // 1. Total Revenue
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        if (totalRevenue == null)
            totalRevenue = BigDecimal.ZERO;

        // 2. Growth Logic (Current Month vs Last Month)
        BigDecimal currentMonthRevenue = orderRepository.sumTotalRevenueByDateRange(startOfMonth, now);
        BigDecimal lastMonthRevenue = orderRepository.sumTotalRevenueByDateRange(startOfLastMonth, endOfLastMonth);

        if (currentMonthRevenue == null)
            currentMonthRevenue = BigDecimal.ZERO;
        if (lastMonthRevenue == null)
            lastMonthRevenue = BigDecimal.ZERO;

        double revenueGrowth = 0;
        if (lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            revenueGrowth = currentMonthRevenue.subtract(lastMonthRevenue)
                    .divide(lastMonthRevenue, 4, java.math.RoundingMode.HALF_UP).doubleValue() * 100;
        } else if (currentMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            revenueGrowth = 100;
        }

        // 3. New Customers (This Month)
        long newCustomers = userRepository.countByCreatedAtBetween(startOfMonth, now);
        long lastMonthCustomers = userRepository.countByCreatedAtBetween(startOfLastMonth, endOfLastMonth);
        double customersGrowth = 0;
        if (lastMonthCustomers > 0) {
            customersGrowth = ((double) (newCustomers - lastMonthCustomers) / lastMonthCustomers) * 100;
        } else if (newCustomers > 0) {
            customersGrowth = 100;
        }

        // 4. Total Orders (This Month count for growth card)
        long currentOrdersCount = orderRepository.countByCreatedAtBetween(startOfMonth, now);
        long lastMonthOrdersCount = orderRepository.countByCreatedAtBetween(startOfLastMonth, endOfLastMonth);
        double ordersGrowth = 0;
        if (lastMonthOrdersCount > 0) {
            ordersGrowth = ((double) (currentOrdersCount - lastMonthOrdersCount) / lastMonthOrdersCount) * 100;
        } else if (currentOrdersCount > 0) {
            ordersGrowth = 100;
        }

        // 5. Total Visits (This Month) and Growth
        long currentVisits = visitLogRepository.countByVisitTimeBetween(startOfMonth, now);
        long lastMonthVisits = visitLogRepository.countByVisitTimeBetween(startOfLastMonth, endOfLastMonth);
        double visitsGrowth = 0;
        if (lastMonthVisits > 0) {
            visitsGrowth = ((double) (currentVisits - lastMonthVisits) / lastMonthVisits) * 100;
        } else if (currentVisits > 0) {
            visitsGrowth = 100;
        }

        // Note: totalVisits in DTO typically means total count for the period or all
        // time.
        // Assuming user wants "Total Visits" card to show CURRENT MONTH visits or ALL
        // TIME?
        // Usually, stats cards like that show a snapshot. Let's show Current Month
        // Visits if it has "Growth" next to it.
        // Or All Time. The image shows "15.234" which looks like All Time.
        // But "Growth +12.5%" implies a period comparison.
        // Let's use All Time for the main number, and growth based on Month-over-Month
        // logic?
        // Logic: Growth is usually Period vs Period.
        // Let's use Current Month Visits for now to be consistent with other stats.
        // If the user wants All Time, we can change it. "Lượt truy cập" could be all
        // time.
        // But if it's "This Month", 15k is a lot.
        // Let's use ALL TIME for the big number, and GROWTH based on This Month vs Last
        // Month trend.
        long totalVisitsAllTime = visitLogRepository.count();

        // 6. Recent Orders
        List<Order> recentOrders = orderRepository.findAll(
                PageRequest.of(0, 5, Sort.by("createdAt").descending())).getContent();
        List<OrderDTO> recentOrderDTOs = recentOrders.stream().map(this::convertToOrderDTO)
                .collect(Collectors.toList());

        return DashboardOverviewDTO.builder()
                .totalRevenue(totalRevenue)
                .totalVisits(totalVisitsAllTime)
                .totalOrders(currentOrdersCount)
                .newCustomers(newCustomers)
                .revenueGrowth(revenueGrowth)
                .visitsGrowth(visitsGrowth)
                .ordersGrowth(ordersGrowth)
                .customersGrowth(customersGrowth)
                .recentOrders(recentOrderDTOs)
                .revenueChartData(Collections.emptyList())
                .build();
    }

    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        if (order.getUser() != null) {
            dto.setCustomerName(order.getUser().getFullName());
            dto.setUserAvatar(
                    order.getUser().getUserProfile() != null ? order.getUser().getUserProfile().getAvatarUrl() : null);
        }
        return dto;
    }
}
