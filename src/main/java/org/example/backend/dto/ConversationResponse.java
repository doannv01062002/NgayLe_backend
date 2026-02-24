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
public class ConversationResponse {
    private Long id;
    private String name;
    private String avatar;
    private String lastMessage;
    private LocalDateTime time;
    private Long unread;
    private Boolean isOnline;
    private String type; // SHOP or USER (Partner type)
    private Long partnerId;
}
