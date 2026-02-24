package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "flash_sale_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private FlashSaleSession flashSaleSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "flash_sale_price", nullable = false)
    private BigDecimal flashSalePrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Quantity allocated for flash sale

    @Column(name = "sold_quantity")
    @Builder.Default
    private Integer soldQuantity = 0;
}
