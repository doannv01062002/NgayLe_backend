package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "shop_id")
    private Long shopId; // Null for platform vouchers

    @Column(nullable = false)
    private String name;

    private String description; // e.g. "Giảm 50k Đồ Trang Trí Noel"

    @Column(nullable = false)
    private String type; // REDEEM (Giảm giá), SHIPPING (Freeship), STOREFRONT (Voucher Shop)

    @Column(nullable = false)
    private String discountType; // PERCENTAGE, FIXED_AMOUNT

    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount; // For percentage based discounts

    private BigDecimal minOrderValue;

    @Column(nullable = false)
    private Integer usageLimit;

    @Builder.Default
    private Integer usageCount = 0;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Builder.Default
    private Boolean isActive = true; // To manually support pausing

    // Metadata for filtering/display
    private String categoryScope; // e.g. "Decor", "All", specific shop ID
    private String userScope; // "All", "New Users", etc.

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
