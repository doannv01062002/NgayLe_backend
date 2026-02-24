package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PromotionType type;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "min_order_value")
    private BigDecimal minOrderValue;

    @Column(name = "max_discount_value")
    private BigDecimal maxDiscountValue;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PromotionStatus status = PromotionStatus.ACTIVE;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    private java.util.List<Order> orders;

    public enum PromotionType {
        PERCENTAGE, FIXED_AMOUNT
    }

    public enum PromotionStatus {
        ACTIVE, INACTIVE, EXPIRED
    }
}
