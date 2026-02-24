package org.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserProfileDTO {
    private Long profileId;
    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;
    private String avatarUrl;
    private String gender;
    private LocalDate dateOfBirth;
    private String bio;
    private String title;
    private String skills;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private String nickname;
    private String nationality;
}
