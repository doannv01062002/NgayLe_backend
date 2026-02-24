package org.example.backend.repository;

import org.example.backend.model.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndOtpCodeAndVerifiedFalseAndExpiresAtAfter(
            String email,
            String otpCode,
            LocalDateTime now);

    Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<EmailVerification> findTopByEmailAndVerifiedTrueAndVerifiedAtAfterOrderByVerifiedAtDesc(
            String email,
            LocalDateTime after);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
