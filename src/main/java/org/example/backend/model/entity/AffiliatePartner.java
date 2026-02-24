package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliate_partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliatePartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to an existing user who became a partner
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String source; // e.g., "Facebook", "TikTok"

    @Enumerated(EnumType.STRING)
    private PartnerStatus status;

    private long totalOrders;

    @Column(precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal totalCommission = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum PartnerStatus {
        PENDING,
        ACTIVE,
        BANNED
    }
}
