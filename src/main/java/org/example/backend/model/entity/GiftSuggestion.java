package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gift_suggestions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggestion_id")
    private Long suggestionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "budget_range")
    private String budgetRange;

    @ElementCollection
    @CollectionTable(name = "gift_suggestion_keywords", joinColumns = @JoinColumn(name = "suggestion_id"))
    @Column(name = "keyword")
    private java.util.List<String> keywords;

    @Column(name = "priority")
    private Integer priority = 0;
}
