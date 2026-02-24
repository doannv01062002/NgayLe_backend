package org.example.backend.service;

import org.example.backend.dto.response.SellerMarketingOverviewResponse;

public interface SellerMarketingService {
    SellerMarketingOverviewResponse getMarketingOverview(Long userId, String period);
}
