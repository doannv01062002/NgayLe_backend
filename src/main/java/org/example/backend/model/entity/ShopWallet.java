package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long walletId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", unique = true, nullable = false)
    private Shop shop;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "frozen_balance")
    private BigDecimal frozenBalance = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
