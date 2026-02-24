package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "reason", nullable = false) // e.g., "Damaged", "Wrong Item"
    private String reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "proof_images_json", columnDefinition = "json")
    private String proofImagesJson;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private ReturnStatus status = ReturnStatus.PENDING;

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "shop_response")
    private String shopResponse;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ReturnStatus {
        PENDING, APPROVED, REJECTED, COMPLETED
    }
}
