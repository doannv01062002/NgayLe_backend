package org.example.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDTO {
    private Long categoryId;
    private Long parentId;
    private String name;
    private String slug;
    private Integer level;
    private String iconUrl;
    private Boolean isHolidaySpecific;
    private Integer displayOrder;
    private List<CategoryDTO> children;
}
