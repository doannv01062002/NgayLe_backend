package org.example.backend.dto;

import lombok.Data;

@Data
public class CreatePartnerRequest {
    private String email;
    private String source;
}
