package org.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductReviewDTO {
    private Long reviewId;
    private Long productId;
    private Long userId;
    private String userName;
    private String userAvatar; // Assuming User entity has avatarUrl or similar
    private Integer rating;
    private String comment;
    private String mediaUrls; // JSON string or list? Entity says String (JSONB)
    private LocalDateTime createdAt;
    private Boolean isHidden;
    private String reply;
    private LocalDateTime replyCreatedAt;
    private String productName;
    private String productImage;
}
