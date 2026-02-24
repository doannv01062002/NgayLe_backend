package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ecard_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ECardTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    // Stores the JSON structure capable of being loaded into a canvas library (like
    // Fabric.js)
    @Column(name = "canvas_data_json", columnDefinition = "TEXT", nullable = false)
    private String canvasDataJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday; // Optional: Link to a specific holiday

    @Column(name = "category") // e.g., "Birthday", "Thank You", "Tet"
    private String category;

    @Column(name = "is_premium")
    @Builder.Default
    private Boolean isPremium = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 0;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
