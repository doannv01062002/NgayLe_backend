package org.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportDTO {
    private Long reportId;
    private Long reporterId;
    private String reporterName;
    private String reporterEmail;
    private String targetType;
    private Long targetId;
    private String targetName; // Optional, might need to fetch
    private String reason;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private String notes; // For admin response/resolution notes (if added to entity later, currently
                          // mostly UI)
}
