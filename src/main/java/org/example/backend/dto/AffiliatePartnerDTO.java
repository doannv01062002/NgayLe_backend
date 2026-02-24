package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffiliatePartnerDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String avatar; // URL
    private String source;
    private long orders;
    private BigDecimal revenue;
    private BigDecimal commission;
    private String status; // PENDING, ACTIVE, BANNED
    private LocalDateTime createdAt;
}
