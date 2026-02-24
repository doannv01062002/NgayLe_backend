package org.example.backend.service;

import org.example.backend.dto.ProductReviewDTO;
import org.example.backend.model.entity.Product;
import org.example.backend.model.entity.ProductReview;
import org.example.backend.model.entity.User;
import org.example.backend.repository.ProductRepository;
import org.example.backend.repository.ProductReviewRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Page<ProductReviewDTO> getReviewsByProduct(Long productId, Pageable pageable) {
        Page<ProductReview> reviews = productReviewRepository.findByProduct_ProductId(productId, pageable);
        return reviews.map(this::mapToDTO);
    }

    @Transactional
    public ProductReviewDTO createProductReview(ProductReviewDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getProductId() == null) {
            throw new RuntimeException("Product ID is required");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductReview review = ProductReview.builder()
                .user(user)
                .product(product)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .mediaUrls(dto.getMediaUrls())
                .createdAt(java.time.LocalDateTime.now())
                .build();

        // Note: Logic to check if user bought product is TEMPORARILY SKIPPED as
        // requested.

        ProductReview savedReview = productReviewRepository.save(review);

        // Update product rating stats
        updateProductRatingStats(product);

        return mapToDTO(savedReview);
    }

    private void updateProductRatingStats(Product product) {
        java.util.List<ProductReview> reviews = productReviewRepository.findByProduct_ProductId(product.getProductId());
        if (reviews.isEmpty())
            return;

        double avg = reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0.0);
        product.setRating(java.math.BigDecimal.valueOf(avg));
        product.setReviewCount(reviews.size());
        productRepository.save(product);
    }

    public Page<ProductReviewDTO> getAdminReviews(String keyword, Integer rating, Pageable pageable) {
        Page<ProductReview> reviews = productReviewRepository.searchReviews(keyword, rating, pageable);
        return reviews.map(this::mapToDTO);
    }

    @Transactional
    public void toggleReviewVisibility(Long reviewId) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setIsHidden(review.getIsHidden() == null ? true : !review.getIsHidden());
        productReviewRepository.save(review);
    }

    @Transactional
    public void replyToReview(Long reviewId, String replyContent) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setReply(replyContent);
        review.setReplyCreatedAt(java.time.LocalDateTime.now());
        productReviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Update product rating before deleting
        Product product = review.getProduct();
        productReviewRepository.delete(review);

        // Recalculate stats after delete (needs flush or fetch fresh list - simplistic
        // approach here)
        // Since delete happens in transaction, we might need to handle this carefully.
        // For now, let's just delete. Recalculation is expensive and maybe done async
        // or scheduled.
        // OR we can manually adjust the product stats if we stored totals, but we store
        // avg.
        // Better to trigger a recalc.
        updateProductRatingStats(product);
    }

    private ProductReviewDTO mapToDTO(ProductReview review) {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setProductId(review.getProduct().getProductId());
        dto.setUserId(review.getUser().getUserId());
        dto.setUserName(review.getUser().getFullName());
        dto.setProductName(review.getProduct().getName());
        dto.setProductImage(
                review.getProduct().getImages().isEmpty() ? "" : review.getProduct().getImages().get(0).getImageUrl());

        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setMediaUrls(review.getMediaUrls());
        dto.setCreatedAt(review.getCreatedAt());

        dto.setIsHidden(review.getIsHidden());
        dto.setReply(review.getReply());
        dto.setReplyCreatedAt(review.getReplyCreatedAt());

        if (review.getUser().getUserProfile() != null) {
            dto.setUserAvatar(review.getUser().getUserProfile().getAvatarUrl());
        }

        return dto;
    }
}
