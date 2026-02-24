package org.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
