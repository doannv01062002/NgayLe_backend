package org.example.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HolidayDTO {
    private Long holidayId;
    private String name;
    private String slug;
    private LocalDate startDate;
    private LocalDate endDate;
    private String themeConfigJson;
    private Boolean isActive;
    private String effectType;
}
