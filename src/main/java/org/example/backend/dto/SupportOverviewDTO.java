package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupportOverviewDTO {
    private long totalArticles;
    private long totalViews;
    private long pendingArticles; // Assuming DRAFT or specific status
}
