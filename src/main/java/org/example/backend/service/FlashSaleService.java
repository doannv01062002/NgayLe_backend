package org.example.backend.service;

import org.example.backend.dto.FlashSaleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FlashSaleService {
    FlashSaleDTO.FlashSaleSessionResponse createSession(FlashSaleDTO.CreateSessionRequest request);

    Page<FlashSaleDTO.FlashSaleSessionResponse> getAllSessions(Pageable pageable);

    FlashSaleDTO.FlashSaleSessionResponse getSession(Long id);

    void deleteSession(Long id);

    void toggleSessionStatus(Long id);

    void addProductsToSession(Long sessionId, List<FlashSaleDTO.AddProductRequest> products);

    void removeProductFromSession(Long flashSaleProductId);

    // For Public User
    FlashSaleDTO.FlashSaleSessionResponse getCurrentFlashSale();

    // Shop Flash Sale
    FlashSaleDTO.FlashSaleSessionResponse createShopSession(Long shopId, FlashSaleDTO.CreateSessionRequest request);

    Page<FlashSaleDTO.FlashSaleSessionResponse> getShopSessions(Long shopId, Pageable pageable);

    void deleteShopSession(Long shopId, Long sessionId);

    void addProductsToShopSession(Long shopId, Long sessionId, List<FlashSaleDTO.AddProductRequest> products);

    FlashSaleDTO.FlashSaleSessionResponse getShopActiveFlashSale(Long shopId);
}
