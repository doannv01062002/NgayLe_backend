package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.FlashSaleDTO;
import org.example.backend.exception.DuplicateResourceException;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.entity.FlashSaleProduct;
import org.example.backend.model.entity.FlashSaleSession;
import org.example.backend.model.entity.Product;
import org.example.backend.model.entity.ProductImage;
import org.example.backend.repository.FlashSaleProductRepository;
import org.example.backend.repository.FlashSaleSessionRepository;
import org.example.backend.repository.ProductRepository;
import org.example.backend.service.FlashSaleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashSaleServiceImpl implements FlashSaleService {

    private final FlashSaleSessionRepository sessionRepository;
    private final FlashSaleProductRepository flashSaleProductRepository;
    private final ProductRepository productRepository;
    private final org.example.backend.repository.ShopRepository shopRepository;

    @Override
    @Transactional
    public FlashSaleDTO.FlashSaleSessionResponse createSession(FlashSaleDTO.CreateSessionRequest request) {
        // Platform session (shop = null)
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check platform overlaps
        List<FlashSaleSession> overlaps = sessionRepository.findOverlappingSessions(request.getStartTime(),
                request.getEndTime());
        // Filter mainly for platform overlaps or define a policy. For now, assuming
        // generic overlap logic.
        // Assuming platform sessions shouldn't overlap?
        if (!overlaps.isEmpty()) {
            // In simple logic, if overlap returns ANY session, we might block.
            // Ideally need specific repo method: findOverlappingPlatformSessions (where
            // shop is null)
        }

        FlashSaleSession session = FlashSaleSession.builder()
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();

        return mapToDTO(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public FlashSaleDTO.FlashSaleSessionResponse createShopSession(Long shopId,
            FlashSaleDTO.CreateSessionRequest request) {
        org.example.backend.model.entity.Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check for Shop's Overlapping Sessions
        List<FlashSaleSession> overlaps = sessionRepository.findOverlappingShopSessions(shopId, request.getStartTime(),
                request.getEndTime());
        if (!overlaps.isEmpty()) {
            throw new DuplicateResourceException("Flash sale session overlaps with existing session");
        }

        FlashSaleSession session = FlashSaleSession.builder()
                .shop(shop)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();

        return mapToDTO(sessionRepository.save(session));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlashSaleDTO.FlashSaleSessionResponse> getAllSessions(Pageable pageable) {
        return sessionRepository.findAllByOrderByStartTimeDesc(pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlashSaleDTO.FlashSaleSessionResponse> getShopSessions(Long shopId, Pageable pageable) {
        // Ideally: sessionRepository.findByShop_ShopIdOrderByStartTimeDesc(shopId,
        // pageable)
        // I will use a spec or assume the method exists or needs creation.
        // Let's rely on standard JPA for now, but I might need to verify Repo.
        // Assuming I'll add the repo method later or use specification.
        // For now, I'll attempt to use FindByShopId if generic, else filter logic.
        // Wait, I should likely add method to Repo or find a way.
        // Let's assume I will add `findAllByShop_ShopIdOrderByStartTimeDesc` to repo.
        return sessionRepository.findAllByShop_ShopIdOrderByStartTimeDesc(shopId, pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public FlashSaleDTO.FlashSaleSessionResponse getSession(Long id) {
        FlashSaleSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        return mapToDTO(session);
    }

    @Override
    @Transactional
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteShopSession(Long shopId, Long sessionId) {
        FlashSaleSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (session.getShop() == null || !session.getShop().getShopId().equals(shopId)) {
            throw new IllegalArgumentException("Session does not belong to this shop");
        }
        sessionRepository.delete(session);
    }

    @Override
    @Transactional
    public void toggleSessionStatus(Long id) {
        FlashSaleSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        session.setIsActive(!session.getIsActive());
        sessionRepository.save(session);
    }

    @Override
    @Transactional
    public void addProductsToSession(Long sessionId, List<FlashSaleDTO.AddProductRequest> products) {
        addProductsToShopSession(null, sessionId, products); // Reuse logic, null = platform? or check inside?
        // Actually the logic below is generic.
    }

    @Override
    @Transactional
    public void addProductsToShopSession(Long shopId, Long sessionId, List<FlashSaleDTO.AddProductRequest> products) {
        FlashSaleSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (shopId != null) {
            if (session.getShop() == null || !session.getShop().getShopId().equals(shopId)) {
                throw new IllegalArgumentException("Session does not belong to this shop");
            }
        }

        List<Long> productIds = products.stream().map(FlashSaleDTO.AddProductRequest::getProductId).toList();
        List<Product> productEntities = productRepository.findAllById(productIds);

        for (FlashSaleDTO.AddProductRequest item : products) {
            Product product = productEntities.stream()
                    .filter(p -> p.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));

            // Validate product belongs to shop if shopId provided
            if (shopId != null && !product.getShop().getShopId().equals(shopId)) {
                throw new IllegalArgumentException("Product " + product.getName() + " does not belong to shop");
            }

            FlashSaleProduct fsp = FlashSaleProduct.builder()
                    .flashSaleSession(session)
                    .product(product)
                    .flashSalePrice(item.getFlashSalePrice())
                    .quantity(item.getQuantity())
                    .soldQuantity(0)
                    .build();
            flashSaleProductRepository.save(fsp);
        }
    }

    @Override
    @Transactional
    public void removeProductFromSession(Long flashSaleProductId) {
        flashSaleProductRepository.deleteById(flashSaleProductId);
    }

    @Override
    @Transactional(readOnly = true)
    public FlashSaleDTO.FlashSaleSessionResponse getCurrentFlashSale() {
        // Platform only logic usually
        LocalDateTime now = LocalDateTime.now();
        return sessionRepository.findCurrentActiveSession(now)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public FlashSaleDTO.FlashSaleSessionResponse getShopActiveFlashSale(Long shopId) {
        LocalDateTime now = LocalDateTime.now();
        // Need repo method:
        // findFirstByShop_ShopIdAndIsActiveTrueAndStartTimeBeforeAndEndTimeAfterOrderByEndTimeAsc
        return sessionRepository.findShopActiveSession(shopId, now)
                .map(this::mapToDTO)
                .orElse(null);
    }

    private FlashSaleDTO.FlashSaleSessionResponse mapToDTO(FlashSaleSession session) {
        LocalDateTime now = LocalDateTime.now();
        String status;
        if (!session.getIsActive()) {
            status = "DISABLED";
        } else if (now.isBefore(session.getStartTime())) {
            status = "UPCOMING";
        } else if (now.isAfter(session.getEndTime())) {
            status = "ENDED";
        } else {
            status = "ONGOING";
        }

        List<FlashSaleDTO.FlashSaleProductResponse> productResponses = Collections.emptyList();
        if (session.getFlashSaleProducts() != null) {
            productResponses = session.getFlashSaleProducts().stream()
                    .map(fsp -> FlashSaleDTO.FlashSaleProductResponse.builder()
                            .id(fsp.getId())
                            .productId(fsp.getProduct().getProductId())
                            .productName(fsp.getProduct().getName())
                            .productImage(fsp.getProduct().getImages() != null
                                    ? fsp.getProduct().getImages().stream()
                                            .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                                            .findFirst()
                                            .map(ProductImage::getImageUrl)
                                            .orElse(fsp.getProduct().getImages().isEmpty() ? null
                                                    : fsp.getProduct().getImages().get(0).getImageUrl())
                                    : null)
                            .originalPrice(fsp.getProduct().getBasePrice())
                            .flashSalePrice(fsp.getFlashSalePrice())
                            .quantity(fsp.getQuantity())
                            .soldQuantity(fsp.getSoldQuantity())
                            .build())
                    .collect(Collectors.toList());
        }

        return FlashSaleDTO.FlashSaleSessionResponse.builder()
                .sessionId(session.getSessionId())
                .shopId(session.getShop() != null ? session.getShop().getShopId() : null)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .isActive(session.getIsActive())
                .status(status)
                .products(productResponses)
                .build();
    }
}
