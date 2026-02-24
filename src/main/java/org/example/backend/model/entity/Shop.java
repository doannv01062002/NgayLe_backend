package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@lombok.ToString(exclude = { "products", "orders", "shopWallet" })
@lombok.EqualsAndHashCode(exclude = { "products", "orders", "shopWallet" })
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long shopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "shop_name", unique = true, nullable = false)
    private String shopName;

    @Column(name = "shop_slug", unique = true, nullable = false)
    private String shopSlug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "shipping_policy", columnDefinition = "TEXT")
    private String shippingPolicy;

    @Column(name = "return_policy", columnDefinition = "TEXT")
    private String returnPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShopStatus status = ShopStatus.PENDING;

    @Column(name = "rating")
    private java.math.BigDecimal rating = java.math.BigDecimal.ZERO;

    @Column(name = "total_sales")
    private Integer totalSales = 0;

    @Column(name = "tax_code")
    private String taxCode;

    @Column(name = "identity_number")
    private String identityNumber;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL)
    private ShopWallet shopWallet;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Order> orders;

    public enum ShopStatus {
        PENDING, ACTIVE, SUSPENDED, CLOSED
    }
}
