package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatContextResponse {
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userAddress;
    private String userAvatar;
    private String rank; // e.g. "Silver"

    // Order info if applicable
    private Long orderId;
    private String orderStatus;
    private Double totalAmount;
    private String orderItemName;
    private String orderItemImage;
    private Integer orderItemCount;
}
