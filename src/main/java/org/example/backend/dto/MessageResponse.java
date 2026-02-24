package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private String content;
    private Long senderId;
    private String senderName;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private String mediaUrl;
    private java.util.List<ReactionDto> reactions;
}
