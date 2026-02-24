package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "shop_daily_analytics", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "shop_id", "date" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopDailyAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Builder.Default
    @Column(name = "visit_count")
    private Long visitCount = 0L;
}
