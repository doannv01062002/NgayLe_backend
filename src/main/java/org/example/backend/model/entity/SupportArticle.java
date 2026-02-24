package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String category;
    private String campaign;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    private String updatedBy; // Name/Username of admin

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "bigint default 0")
    @Builder.Default
    private Long viewCount = 0L;

    public enum ArticleStatus {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }
}
