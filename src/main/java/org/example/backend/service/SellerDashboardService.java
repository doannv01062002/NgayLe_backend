package org.example.backend.service;

import org.example.backend.dto.response.SellerDashboardResponse;

public interface SellerDashboardService {
    SellerDashboardResponse getDashboardStats(Long userId, String period);
}
