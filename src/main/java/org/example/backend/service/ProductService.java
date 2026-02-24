package org.example.backend.service;

import org.example.backend.dto.ProductDTO;
import org.example.backend.dto.ProductVariantDTO;
import org.example.backend.model.entity.Product;
import org.example.backend.model.entity.ProductImage;
import org.example.backend.model.entity.ProductVariant;
import org.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private org.example.backend.repository.CategoryRepository categoryRepository;

    @Autowired
    private org.example.backend.repository.ShopRepository shopRepository;

    @Autowired
    private org.example.backend.repository.UserRepository userRepository; // Just in case, though usually ShopRepository
                                                                          // matches by Owner

    @Transactional
    public ProductDTO createProduct(org.example.backend.dto.ProductCreateRequest request, String ownerEmail) {
        // 1. Find Shop by Owner Email
        org.example.backend.model.entity.User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        org.example.backend.model.entity.Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("Shop not found for this user"));

        // 2. Find Category
        org.example.backend.model.entity.Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 3. Create Product Entity
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .originalPrice(request.getOriginalPrice())
                .shop(shop)
                .category(category)
                .status(Product.ProductStatus.ACTIVE) // Default active for now, or DRAFT
                .targetAudience(request.getTargetAudience())
                .giftOccasion(request.getGiftOccasion())
                .brand(request.getBrand())
                .origin(request.getOrigin())
                // New Fields
                .sku(request.getSku())
                .videoUrl(request.getVideoUrl())
                .weight(request.getWeight())
                .height(request.getHeight())
                .width(request.getWidth())
                .length(request.getLength())
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .build();

        // 4. Handle Images
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            java.util.List<ProductImage> images = request.getImageUrls().stream().map(url -> ProductImage.builder()
                    .imageUrl(url)
                    .product(product)
                    .build()).collect(Collectors.toList());
            product.setImages(images);
        }

        // 5. Handle Variants
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            java.util.List<ProductVariant> variants = request.getVariants().stream()
                    .map(vDto -> ProductVariant.builder()
                            .sku(vDto.getSku())
                            .name(vDto.getName())
                            .price(vDto.getPrice())
                            .originalPrice(vDto.getOriginalPrice())
                            .stockQuantity(vDto.getStockQuantity())
                            .imageUrl(vDto.getImageUrl())
                            .option1Name(vDto.getOption1Name())
                            .option1Value(vDto.getOption1Value())
                            .option2Name(vDto.getOption2Name())
                            .option2Value(vDto.getOption2Value())
                            .product(product)
                            .build())
                    .collect(Collectors.toList());
            product.setVariants(variants);
        } else {
            // Create default variant if variants are empty (Simple Product Mode)
            Integer stock = request.getStock() != null ? request.getStock() : 0;
            ProductVariant defaultVariant = ProductVariant.builder()
                    .sku("SKU-" + System.currentTimeMillis()) // Simple auto-generation
                    .name("Mặc định")
                    .price(request.getBasePrice())
                    .originalPrice(request.getOriginalPrice())
                    .stockQuantity(stock)
                    .imageUrl(request.getImageUrls() != null && !request.getImageUrls().isEmpty()
                            ? request.getImageUrls().get(0)
                            : null)
                    .product(product)
                    .build();
            product.setVariants(java.util.Collections.singletonList(defaultVariant));
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public Page<ProductDTO> getProductsByShop(String ownerEmail, String search, String statusStr, Boolean outOfStock,
            Long categoryId, Integer minStock, Integer maxStock, Pageable pageable) {
        org.example.backend.model.entity.User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        org.example.backend.model.entity.Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("Shop not found for this user"));

        Product.ProductStatus status = null;
        if (statusStr != null && !statusStr.isEmpty() && !statusStr.equalsIgnoreCase("all")) {
            try {
                status = Product.ProductStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        java.util.List<Long> categoryIds = null;
        if (categoryId != null) {
            categoryIds = java.util.Collections.singletonList(categoryId);
        }

        org.springframework.data.jpa.domain.Specification<Product> spec = org.example.backend.repository.specification.ProductSpecification
                .filterBy(search, categoryIds, null, null, null, shop.getShopId(), status, outOfStock, minStock,
                        maxStock);

        return productRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    public Page<ProductDTO> getProductsByShopId(Long shopId, Pageable pageable) {
        org.example.backend.model.entity.Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        org.springframework.data.jpa.domain.Specification<Product> spec = org.example.backend.repository.specification.ProductSpecification
                .filterBy(null, null, null, null, null, shop.getShopId(), Product.ProductStatus.ACTIVE, null, null,
                        null);

        return productRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    public Page<ProductDTO> getAllProducts(
            String search,
            String categorySlug,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            Double rating,
            Pageable pageable) {

        java.util.List<Long> categoryIds = null;
        if (categorySlug != null && !categorySlug.isEmpty()) {
            org.example.backend.model.entity.Category category = categoryRepository.findBySlug(categorySlug);
            if (category == null) {
                return Page.empty(pageable);
            }
            categoryIds = new java.util.ArrayList<>();
            collectCategoryIds(category, categoryIds);
        }

        Product.ProductStatus status = Product.ProductStatus.ACTIVE;

        org.springframework.data.jpa.domain.Specification<Product> spec = org.example.backend.repository.specification.ProductSpecification
                .filterBy(search, categoryIds, minPrice, maxPrice, rating, null, status, null, null, null);

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(this::convertToDTO);
    }

    public Page<ProductDTO> getAdminProducts(String search, String statusStr, Pageable pageable) {
        Product.ProductStatus status = null;
        if (statusStr != null && !statusStr.isEmpty() && !statusStr.equalsIgnoreCase("all")) {
            try {
                status = Product.ProductStatus.valueOf(statusStr.toUpperCase());
            } catch (Exception e) {
            }
        }

        org.springframework.data.jpa.domain.Specification<Product> spec = org.example.backend.repository.specification.ProductSpecification
                .filterBy(search, null, null, null, null, null, status, null, null, null);

        return productRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    @Transactional
    public ProductDTO updateProductStatus(Long id, String statusStr) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        try {
            product.setStatus(Product.ProductStatus.valueOf(statusStr));
            return convertToDTO(productRepository.save(product));
        } catch (Exception e) {
            throw new RuntimeException("Invalid status");
        }
    }

    public java.util.Map<String, Long> getProductStats(String ownerEmail) {
        org.example.backend.model.entity.User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        org.example.backend.model.entity.Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        java.util.Map<String, Long> stats = new java.util.HashMap<>();

        // Helper to count with spec
        // All
        stats.put("all", productRepository.count(org.example.backend.repository.specification.ProductSpecification
                .filterBy(null, null, null, null, null, shop.getShopId(), null, null, null, null)));

        // Active
        stats.put("active",
                productRepository.count(org.example.backend.repository.specification.ProductSpecification.filterBy(null,
                        null, null, null, null, shop.getShopId(), Product.ProductStatus.ACTIVE, null, null, null)));

        // Pending
        stats.put("pending",
                productRepository.count(
                        org.example.backend.repository.specification.ProductSpecification.filterBy(null, null, null,
                                null, null, shop.getShopId(), Product.ProductStatus.PENDING_REVIEW, null, null, null)));

        // Out of Stock (Active + OutOfStock)
        stats.put("outOfStock",
                productRepository.count(org.example.backend.repository.specification.ProductSpecification.filterBy(null,
                        null, null, null, null, shop.getShopId(), Product.ProductStatus.ACTIVE, true, null, null)));

        // Violation
        stats.put("violation",
                productRepository.count(
                        org.example.backend.repository.specification.ProductSpecification.filterBy(null, null, null,
                                null, null, shop.getShopId(), Product.ProductStatus.BANNED, null, null, null)));

        return stats;
    }

    @Autowired
    private org.example.backend.repository.ReportRepository reportRepository;

    public org.example.backend.dto.StatsDTO getAdminProductStats() {
        org.example.backend.dto.StatsDTO stats = new org.example.backend.dto.StatsDTO();

        // Total
        stats.setTotal(productRepository.count());

        // Pending
        stats.setPending(productRepository
                .count((root, query, cb) -> cb.equal(root.get("status"), Product.ProductStatus.PENDING_REVIEW)));

        // Reported (Real count of PENDING reports for PRODUCTS)
        // Note: This counts REPORTS, not unique reported PRODUCTS.
        // For distinct products, we would need a custom query, but this is sufficient
        // for 'Reported Violations' metric.
        stats.setReported(reportRepository.countByTargetTypeAndStatus(
                org.example.backend.model.entity.Report.TargetType.PRODUCT,
                org.example.backend.model.entity.Report.ReportStatus.PENDING));

        // Removed / Banned
        stats.setBanned(productRepository
                .count((root, query, cb) -> cb.equal(root.get("status"), Product.ProductStatus.BANNED)));

        // Active
        stats.setActive(productRepository
                .count((root, query, cb) -> cb.equal(root.get("status"), Product.ProductStatus.ACTIVE)));

        return stats;
    }

    @Transactional
    public void bulkDeleteProducts(List<Long> ids, String ownerEmail) {
        org.example.backend.model.entity.User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Product> products = productRepository.findAllById(ids);
        for (Product p : products) {
            if (p.getShop().getOwner().getUserId().equals(owner.getUserId())) {
                productRepository.delete(p);
            }
        }
    }

    @Transactional
    public void bulkUpdateStatus(List<Long> ids, String statusStr, String ownerEmail) {
        org.example.backend.model.entity.User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product.ProductStatus status;
        try {
            status = Product.ProductStatus.valueOf(statusStr);
        } catch (Exception e) {
            throw new RuntimeException("Invalid status");
        }

        List<Product> products = productRepository.findAllById(ids);
        for (Product p : products) {
            if (p.getShop().getOwner().getUserId().equals(owner.getUserId())) {
                p.setStatus(status);
            }
        }
        productRepository.saveAll(products);
    }

    private void collectCategoryIds(org.example.backend.model.entity.Category category, java.util.List<Long> ids) {
        ids.add(category.getCategoryId());
        if (category.getChildren() != null) {
            for (org.example.backend.model.entity.Category child : category.getChildren()) {
                collectCategoryIds(child, ids);
            }
        }
    }

    @Transactional
    public ProductDTO updateProduct(Long id, org.example.backend.dto.ProductCreateRequest request, String ownerEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        org.example.backend.model.entity.User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!product.getShop().getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You are not the owner of this product");
        }

        // Update basic fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setTargetAudience(request.getTargetAudience());
        product.setGiftOccasion(request.getGiftOccasion());
        product.setBrand(request.getBrand());
        product.setOrigin(request.getOrigin());
        // Map new fields
        product.setSku(request.getSku());
        product.setVideoUrl(request.getVideoUrl());
        product.setWeight(request.getWeight());
        product.setHeight(request.getHeight());
        product.setWidth(request.getWidth());
        product.setLength(request.getLength());
        product.setMetaTitle(request.getMetaTitle());
        product.setMetaDescription(request.getMetaDescription());

        if (request.getCategoryId() != null) {
            org.example.backend.model.entity.Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        // Update Images (replace all)
        if (request.getImageUrls() != null) {
            // Remove old images
            if (product.getImages() != null) {
                product.getImages().clear();
            }
            // Add new
            java.util.List<ProductImage> images = request.getImageUrls().stream().map(url -> ProductImage.builder()
                    .imageUrl(url)
                    .product(product)
                    .build()).collect(Collectors.toList());
            if (product.getImages() == null) {
                product.setImages(images);
            } else {
                product.getImages().addAll(images);
            }
        }

        // Update Stock (Default Variant)
        // Check if strictly simple product (1 variant)
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            // Update the first variant or "Default" variant
            ProductVariant defaultVariant = product.getVariants().get(0);
            Integer stock = request.getStock() != null ? request.getStock() : 0;
            defaultVariant.setPrice(request.getBasePrice());
            defaultVariant.setOriginalPrice(request.getOriginalPrice());
            defaultVariant.setStockQuantity(stock);
            // Update image of variant to first product image if available
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                defaultVariant.setImageUrl(request.getImageUrls().get(0));
            }
        } else {
            // Create if missing (edge case)
            Integer stock = request.getStock() != null ? request.getStock() : 0;
            ProductVariant defaultVariant = ProductVariant.builder()
                    .sku("SKU-" + System.currentTimeMillis())
                    .name("Mặc định")
                    .price(request.getBasePrice())
                    .originalPrice(request.getOriginalPrice())
                    .stockQuantity(stock)
                    .imageUrl(request.getImageUrls() != null && !request.getImageUrls().isEmpty()
                            ? request.getImageUrls().get(0)
                            : null)
                    .product(product)
                    .build();
            product.setVariants(java.util.Collections.singletonList(defaultVariant));
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id, String ownerEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        org.example.backend.model.entity.User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!product.getShop().getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You are not the owner of this product");
        }

        productRepository.delete(product);
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToDTO(product);
    }

    @Autowired
    private org.example.backend.repository.ShopDailyAnalyticsRepository shopDailyAnalyticsRepository;

    public Page<ProductDTO> getGiftSuggestions(String recipient, String occasion, java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findGiftSuggestions(recipient, occasion, minPrice, maxPrice, pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public void recordVisit(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        org.example.backend.model.entity.Shop shop = product.getShop();
        if (shop == null)
            return;

        java.time.LocalDate today = java.time.LocalDate.now();
        org.example.backend.model.entity.ShopDailyAnalytics analytics = shopDailyAnalyticsRepository
                .findByShop_ShopIdAndDate(shop.getShopId(), today)
                .orElse(org.example.backend.model.entity.ShopDailyAnalytics.builder()
                        .shop(shop)
                        .date(today)
                        .visitCount(0L)
                        .build());

        analytics.setVisitCount(analytics.getVisitCount() + 1);
        shopDailyAnalyticsRepository.save(analytics);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBasePrice(product.getBasePrice());
        dto.setPromotionalPrice(product.getPromotionalPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setIsHolidaySuggestion(product.getIsHolidaySuggestion());
        dto.setRating(product.getRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setSoldCount(product.getSoldCount());
        if (product.getStatus() != null) {
            dto.setStatus(product.getStatus().name());
        }
        dto.setCreatedAt(product.getCreatedAt());
        dto.setTargetAudience(product.getTargetAudience());
        dto.setGiftOccasion(product.getGiftOccasion());
        dto.setBrand(product.getBrand());
        dto.setOrigin(product.getOrigin());
        // Map new fields to DTO
        dto.setSku(product.getSku());
        dto.setVideoUrl(product.getVideoUrl());
        dto.setWeight(product.getWeight());
        dto.setHeight(product.getHeight());
        dto.setWidth(product.getWidth());
        dto.setLength(product.getLength());
        dto.setMetaTitle(product.getMetaTitle());
        dto.setMetaDescription(product.getMetaDescription());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
            dto.setCategorySlug(product.getCategory().getSlug());
        }
        if (product.getShop() != null) {
            dto.setShopName(product.getShop().getShopName());
            dto.setShopId(product.getShop().getShopId());
            dto.setShopLogoUrl(product.getShop().getLogoUrl());
        }

        if (product.getVariants() != null) {
            List<ProductVariantDTO> variantDTOs = product.getVariants().stream().map(v -> {
                ProductVariantDTO vDto = new ProductVariantDTO();
                vDto.setVariantId(v.getVariantId());
                vDto.setSku(v.getSku());
                vDto.setName(v.getName());
                vDto.setOption1Name(v.getOption1Name());
                vDto.setOption1Value(v.getOption1Value());
                vDto.setOption2Name(v.getOption2Name());
                vDto.setOption2Value(v.getOption2Value());
                vDto.setOption2Value(v.getOption2Value());
                vDto.setPrice(v.getPrice());
                vDto.setOriginalPrice(v.getOriginalPrice());
                vDto.setStockQuantity(v.getStockQuantity());
                vDto.setImageUrl(v.getImageUrl());
                return vDto;
            }).collect(Collectors.toList());
            dto.setVariants(variantDTOs);
        }

        if (product.getImages() != null) {
            dto.setImageUrls(product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
        }

        return dto;
    }
}
