package org.example.backend.service.impl;

import org.example.backend.dto.seller.SellerOrderDTO;
import org.example.backend.model.entity.Order;
import org.example.backend.repository.OrderRepository;
import org.example.backend.service.SellerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SellerOrderServiceImpl implements SellerOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Page<SellerOrderDTO> getSellerOrders(Long userId, String statusStr, String keyword, String startDateStr,
            String endDateStr, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Order.OrderStatus status = null;
        if (statusStr != null && !statusStr.isEmpty() && !statusStr.equals("ALL")) {
            try {
                status = Order.OrderStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        // Parse dates if needed
        // Parse dates if needed
        java.time.LocalDateTime startDate = null;
        java.time.LocalDateTime endDate = null;

        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                startDate = java.time.LocalDateTime.parse(startDateStr);
            } catch (Exception e) {
                // Try simpler format if needed or ignore
                System.out.println("Invalid start date format: " + startDateStr);
            }
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                endDate = java.time.LocalDateTime.parse(endDateStr);
            } catch (Exception e) {
                System.out.println("Invalid end date format: " + endDateStr);
            }
        }

        if (statusStr != null && statusStr.equals("ALL")) {
            status = null;
        }

        Page<Order> orders = orderRepository.findSellerOrders(userId, status, keyword, startDate, endDate, pageable);

        return orders.map(this::mapToDTO);
    }

    @Override
    @Transactional
    public SellerOrderDTO updateOrderStatus(Long userId, Long orderId, String newStatusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify ownership
        if (!order.getShop().getOwner().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to order");
        }

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(newStatusStr);

        // Simple state transition logic
        // In a real app, you'd check if transition is valid (e.g. valid PENDING ->
        // PROCESSING)
        order.setStatus(newStatus);

        // If status is SHIPPING, maybe generate tracking code if null
        if (newStatus == Order.OrderStatus.SHIPPING && order.getTrackingCode() == null) {
            order.setTrackingCode("SHIP" + System.currentTimeMillis());
        }

        orderRepository.save(order);
        return mapToDTO(order);
    }

    @Override
    @Transactional
    public void bulkUpdateStatus(Long userId, List<Long> orderIds, String newStatusStr) {
        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(newStatusStr);
        List<Order> orders = orderRepository.findAllById(orderIds);

        for (Order order : orders) {
            if (order.getShop().getOwner().getUserId().equals(userId)) {
                order.setStatus(newStatus);
                orderRepository.save(order);
            }
        }
    }

    private SellerOrderDTO mapToDTO(Order order) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        List<SellerOrderDTO.SellerOrderItemDTO> items = order.getOrderItems().stream()
                .map(item -> SellerOrderDTO.SellerOrderItemDTO.builder()
                        .name(item.getProductName())
                        .variant(item.getVariantName() != null ? item.getVariantName() : "Mặc định")
                        .quantity(item.getQuantity())
                        .price(currencyFormatter.format(item.getUnitPrice()))
                        .image(item.getProductVariant().getImageUrl() != null ? item.getProductVariant().getImageUrl()
                                : getProductThumbnail(item.getProduct()))
                        .build())
                .collect(Collectors.toList());

        String displayId = order.getTrackingCode() != null ? order.getTrackingCode() : order.getOrderId().toString();

        return SellerOrderDTO.builder()
                .id(order.getOrderId())
                .orderId(displayId)
                .buyerName(order.getUser().getFullName())
                .status(mapStatusToLabel(order.getStatus()))
                .statusKey(order.getStatus().name())
                .items(items)
                .total(currencyFormatter.format(order.getTotalAmount()))
                .paymentMethod(order.getPaymentMethod().name()) // Or localized
                .shippingMethod(order.getShippingCarrier() != null ? order.getShippingCarrier() : "Tiêu chuẩn")
                .createdAt(order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .note(order.getGiftMessage()) // Using gift message as note for now? Or need a separate note field.
                                              // Order has no 'note' field in entity shown.
                .build();
    }

    private String getProductThumbnail(org.example.backend.model.entity.Product product) {
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            return product.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                    .findFirst()
                    .map(org.example.backend.model.entity.ProductImage::getImageUrl)
                    .orElse(product.getImages().get(0).getImageUrl());
        }
        return ""; // Or default placeholder
    }

    private String mapStatusToLabel(Order.OrderStatus status) {
        switch (status) {
            case PENDING_PAYMENT:
                return "Chờ thanh toán";
            case PROCESSING:
                return "Chờ xác nhận"; // Mapping PROCESSING to "Chờ xác nhận"
            case SHIPPING:
                return "Đang giao";
            case DELIVERED:
                return "Đã giao";
            case CANCELLED:
                return "Đã hủy";
            case COMPLETED:
                return "Hoàn thành";
            case RETURNED:
                return "Trả hàng/Hoàn tiền";
            case PAID:
                return "Đã thanh toán";
            default:
                return status.name();
        }
    }
}
