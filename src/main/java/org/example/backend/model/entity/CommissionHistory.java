package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private AffiliatePartner partner;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    // EARNED, WITHDRAWN, ADJUSTMENT
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TransactionType {
        EARNED,
        WITHDRAWN,
        ADJUSTMENT
    }
}
