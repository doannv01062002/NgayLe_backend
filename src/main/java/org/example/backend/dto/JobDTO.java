package org.example.backend.dto;

import lombok.Data;
import org.example.backend.model.entity.Job;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobDTO {
    private Long jobId;
    private Long hirerId;
    private String hirerName;
    private String title;
    private String description;
    private String location;
    private BigDecimal budget;
    private LocalDateTime deadline;
    private Job.Status status;
    private LocalDateTime createdAt;
}
