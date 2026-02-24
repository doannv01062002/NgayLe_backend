package org.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "gender")
    private String gender;

    @Column(name = "dob")
    private java.time.LocalDate dateOfBirth;

    @Column(name = "loyalty_points")
    @Builder.Default
    private Integer loyaltyPoints = 0;

    @Column(name = "wallet_balance")
    @Builder.Default
    private java.math.BigDecimal walletBalance = java.math.BigDecimal.ZERO;

    @Column(name = "preferences_json", columnDefinition = "json")
    private String preferencesJson;

    // Worker specific fields
    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "title")
    private String title;

    @Column(name = "skills")
    private String skills;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "hourly_rate")
    private java.math.BigDecimal hourlyRate;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "nationality")
    private String nationality;
}
