package org.example.backend.service;

import org.example.backend.dto.HomeFeatureProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HomeFeatureService {
    // Admin
    Page<HomeFeatureProductDTO> getFeatureProducts(String sectionType, Pageable pageable);

    void addProductToSection(Long productId, String sectionType);

    void removeProductFromSection(Long id);

    void updateDisplayOrder(Long id, Integer newOrder);

    // Public
    List<HomeFeatureProductDTO> getPublicSection(String sectionType);
}
