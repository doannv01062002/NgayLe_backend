package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.model.entity.Banner;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {
    private Long bannerId;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Banner.BannerPosition position;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long views;
    private Long clickCount;
    private Double ctr;

    public Double getCtr() {
        if (views == null || views == 0)
            return 0.0;
        return (double) clickCount / views * 100;
    }
}
