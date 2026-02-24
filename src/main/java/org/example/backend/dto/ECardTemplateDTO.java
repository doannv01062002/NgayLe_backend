package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ECardTemplateDTO {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private String category;
    private Boolean isPremium;
    private Boolean isActive;
    private Integer usageCount;
    private LocalDateTime createdAt;
    // We might not send the full canvas JSON in the list view to save bandwidth
    private String canvasDataJson;
}
