package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flash_sale_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true; // Use this to manually turn off a session if needed

    // Helper status for UI/Logic
    @Column(name = "status")
    private String status; // UPCOMING, ONGOING, ENDED (Can be derived or persisted for caching)

    @OneToMany(mappedBy = "flashSaleSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlashSaleProduct> flashSaleProducts;
}
