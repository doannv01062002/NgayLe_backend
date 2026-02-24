package org.example.backend.service;

import org.example.backend.dto.AdminShopDTO;
import org.example.backend.model.entity.Shop;
import org.example.backend.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminShopService {

    @Autowired
    private ShopRepository shopRepository;

    public Page<AdminShopDTO> getShops(String search, String statusStr, Pageable pageable) {
        Specification<Shop> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String searchLower = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("shopName")),
                        searchLower);
                // Also search by owner email if needed, but requires join. kept simple for now
                predicates.add(namePredicate);
            }

            if (statusStr != null && !statusStr.isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
                predicates.add(criteriaBuilder.equal(root.get("status"), Shop.ShopStatus.valueOf(statusStr)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return shopRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    public void updateShopStatus(Long shopId, String statusStr) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        shop.setStatus(Shop.ShopStatus.valueOf(statusStr));
        shopRepository.save(shop);
    }

    @Autowired
    private org.example.backend.repository.ReportRepository reportRepository;

    public org.example.backend.dto.StatsDTO getShopStats() {
        org.example.backend.dto.StatsDTO stats = new org.example.backend.dto.StatsDTO();

        // Total
        stats.setTotal(shopRepository.count());

        // Active
        stats.setActive(
                shopRepository.count((root, query, cb) -> cb.equal(root.get("status"), Shop.ShopStatus.ACTIVE)));

        // Pending
        stats.setPending(
                shopRepository.count((root, query, cb) -> cb.equal(root.get("status"), Shop.ShopStatus.PENDING)));

        // Suspended/Closed -> Banned
        stats.setBanned(shopRepository.count((root, query, cb) -> cb.or(
                cb.equal(root.get("status"), Shop.ShopStatus.SUSPENDED),
                cb.equal(root.get("status"), Shop.ShopStatus.CLOSED))));

        // Reported Shops
        stats.setReported(reportRepository.countByTargetTypeAndStatus(
                org.example.backend.model.entity.Report.TargetType.SHOP,
                org.example.backend.model.entity.Report.ReportStatus.PENDING));

        return stats;
    }

    private AdminShopDTO convertToDTO(Shop shop) {
        AdminShopDTO dto = new AdminShopDTO();
        dto.setShopId(shop.getShopId());
        dto.setShopName(shop.getShopName());
        if (shop.getOwner() != null) {
            dto.setOwnerName(shop.getOwner().getFullName());
            dto.setOwnerEmail(shop.getOwner().getEmail());
        }
        dto.setLogoUrl(shop.getLogoUrl());
        dto.setStatus(shop.getStatus().name());
        dto.setTotalSales(shop.getTotalSales());
        dto.setRating(shop.getRating());
        dto.setTaxCode(shop.getTaxCode());
        dto.setCreatedAt(shop.getCreatedAt());
        return dto;
    }
}
