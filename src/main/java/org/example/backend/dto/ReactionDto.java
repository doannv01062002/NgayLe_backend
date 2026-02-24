package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.model.enums.ReactionType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionDto {
    private Long id;
    private Long userId;
    private String userName;
    private ReactionType type;
}
