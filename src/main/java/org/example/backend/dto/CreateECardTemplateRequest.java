package org.example.backend.dto;

import lombok.Data;

@Data
public class CreateECardTemplateRequest {
    private String name;
    private String category;
    private Boolean isPremium;
    private String canvasDataJson; // Optional if we just upload an image
    // Image will be handled via MultipartFile, this DTO is for metadata or JSON
    // body if separate
}
