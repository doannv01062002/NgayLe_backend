package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.HomeFeatureProductDTO;
import org.example.backend.exception.DuplicateResourceException;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.entity.HomeFeatureProduct;
import org.example.backend.model.entity.Product;
import org.example.backend.model.entity.ProductImage;
import org.example.backend.repository.HomeFeatureProductRepository;
import org.example.backend.repository.ProductRepository;
import org.example.backend.service.HomeFeatureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeFeatureServiceImpl implements HomeFeatureService {

    private final HomeFeatureProductRepository featureRepo;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<HomeFeatureProductDTO> getFeatureProducts(String sectionType, Pageable pageable) {
        return featureRepo.findAllBySectionTypeOrderByDisplayOrderAsc(sectionType, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void addProductToSection(Long productId, String sectionType) {
        if (featureRepo.existsByProduct_ProductIdAndSectionType(productId, sectionType)) {
            throw new DuplicateResourceException("Product already in this section");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        HomeFeatureProduct feature = HomeFeatureProduct.builder()
                .product(product)
                .sectionType(sectionType)
                .displayOrder(0) // Default top
                .build();
        featureRepo.save(feature);
    }

    @Override
    @Transactional
    public void removeProductFromSection(Long id) {
        featureRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void updateDisplayOrder(Long id, Integer newOrder) {
        HomeFeatureProduct feature = featureRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found"));
        feature.setDisplayOrder(newOrder);
        featureRepo.save(feature);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeFeatureProductDTO> getPublicSection(String sectionType) {
        return featureRepo.findAllBySectionTypeOrderByDisplayOrderAsc(sectionType).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private HomeFeatureProductDTO mapToDTO(HomeFeatureProduct entity) {
        return HomeFeatureProductDTO.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getProductId())
                .productName(entity.getProduct().getName())
                .productImage(entity.getProduct().getImages() != null
                        ? entity.getProduct().getImages().stream()
                                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                                .findFirst()
                                .map(ProductImage::getImageUrl)
                                .orElse(entity.getProduct().getImages().isEmpty() ? null
                                        : entity.getProduct().getImages().get(0).getImageUrl())
                        : null)
                .price(entity.getProduct().getBasePrice())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }
}
