package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginRequest {
    private String provider; // "google" or "facebook"
    private String accessToken;
    private String email;
    private String fullName;
    private String providerId; // Social provider's unique user ID
}
