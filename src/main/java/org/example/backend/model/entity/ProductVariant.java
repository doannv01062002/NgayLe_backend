package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@lombok.ToString(exclude = { "cartItems", "orderItems", "product" })
@lombok.EqualsAndHashCode(exclude = { "cartItems", "orderItems", "product" })
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Long variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", unique = true)
    private String sku;

    @Column(name = "name")
    private String name;

    @Column(name = "option1_name")
    private String option1Name;

    @Column(name = "option1_value")
    private String option1Value;

    @Column(name = "option2_name")
    private String option2Name;

    @Column(name = "option2_value")
    private String option2Value;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL)
    private java.util.List<CartItem> cartItems;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL)
    private java.util.List<OrderItem> orderItems;
}
