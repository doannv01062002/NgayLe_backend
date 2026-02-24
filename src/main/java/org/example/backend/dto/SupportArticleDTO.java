package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupportArticleDTO {
    private Long id;
    private String title;
    private String content; // HTML content
    private String category;
    private String campaign;
    private String status; // DRAFT, PUBLISHED, ARCHIVED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
