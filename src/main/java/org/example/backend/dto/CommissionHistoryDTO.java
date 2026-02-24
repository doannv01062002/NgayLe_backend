package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CommissionHistoryDTO {
    private Long id;
    private Long partnerId;
    private String partnerName;
    private BigDecimal amount;
    private String type;
    private String description;
    private LocalDateTime createdAt;
}
