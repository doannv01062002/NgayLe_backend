package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ECardStatsDTO {
    private long totalTemplates;
    private long totalUsage;
    private ECardTemplateDTO popularTemplate;
    // Add more as needed
}
