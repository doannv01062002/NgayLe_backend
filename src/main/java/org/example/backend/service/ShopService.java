package org.example.backend.service;

import org.example.backend.dto.ShopRegisterRequest;
import org.example.backend.dto.ShopResponse;
import org.example.backend.dto.ShopUpdateRequest;

public interface ShopService {
    ShopResponse registerShop(ShopRegisterRequest request, String userEmail);

    ShopResponse getCurrentShop(String userEmail);

    ShopResponse getShopById(Long shopId);

    ShopResponse updateShop(ShopUpdateRequest request, String userEmail);
}
