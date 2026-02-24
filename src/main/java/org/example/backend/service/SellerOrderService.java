package org.example.backend.service;

import org.example.backend.dto.seller.SellerOrderDTO;
import org.example.backend.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerOrderService {
    Page<SellerOrderDTO> getSellerOrders(Long userId, String status, String keyword, String startDate, String endDate,
            int page, int size);

    SellerOrderDTO updateOrderStatus(Long userId, Long orderId, String newStatus);

    void bulkUpdateStatus(Long userId, java.util.List<Long> orderIds, String newStatus);
}
