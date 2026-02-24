package org.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminShopDTO {
    private Long shopId;
    private String shopName;
    private String ownerName;
    private String ownerEmail;
    private String logoUrl;
    private String status;
    private Integer totalSales;
    private BigDecimal rating;
    private String taxCode;
    private LocalDateTime createdAt;
}
