package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@lombok.ToString(exclude = { "orderItems", "user", "shop", "promotion" })
@lombok.EqualsAndHashCode(exclude = { "orderItems", "user", "shop", "promotion" })
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    // Financials
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    // Shipping Info
    @Column(name = "shipping_address_json", columnDefinition = "TEXT", nullable = false)
    private String shippingAddressJson;

    @Column(name = "shipping_carrier")
    private String shippingCarrier;

    @Column(name = "tracking_code")
    private String trackingCode;

    // Gift Options
    @Column(name = "is_gift")
    private Boolean isGift = false;

    @Column(name = "gift_message", columnDefinition = "TEXT")
    private String giftMessage;

    @Column(name = "gift_wrap_option")
    private String giftWrapOption;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private java.util.List<OrderItem> orderItems;

    public enum OrderStatus {
        PENDING_PAYMENT, PAID, PROCESSING, SHIPPING, DELIVERED, COMPLETED, CANCELLED, RETURNED
    }

    public enum PaymentStatus {
        UNPAID, PAID, REFUNDED
    }

    public enum PaymentMethod {
        COD, MOMO, VNPAY, BANK_TRANSFER, WALLET
    }
}
