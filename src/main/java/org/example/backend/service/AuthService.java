package org.example.backend.service;

import org.example.backend.dto.*;
import org.example.backend.model.entity.EmailVerification;
import org.example.backend.model.entity.User;
import org.example.backend.repository.EmailVerificationRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.security.CustomUserDetails;
import org.example.backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private EmailVerificationRepository emailVerificationRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private EmailService emailService;

        @Value("${app.otp.expiration:300000}")
        private Long otpExpiration;

        // Send OTP to email
        @Transactional
        public void sendOtp(String email) {
                // Generate 6-digit OTP
                String otpCode = String.format("%06d", new Random().nextInt(999999));

                // Calculate expiration
                LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(otpExpiration / 1000);

                // Save to database
                EmailVerification verification = EmailVerification.builder()
                                .email(email)
                                .otpCode(otpCode)
                                .expiresAt(expiresAt)
                                .verified(false)
                                .build();

                emailVerificationRepository.save(verification);

                // Send email
                emailService.sendOtpEmail(email, otpCode);
        }

        // Send OTP for password reset (checks if user exists)
        @Transactional
        public void sendPasswordResetOtp(String email) {
                if (!userRepository.existsByEmail(email)) {
                        throw new RuntimeException("Email không tồn tại trong hệ thống");
                }
                sendOtp(email);
        }

        // Verify OTP
        @Transactional
        public boolean verifyOtp(String email, String otpCode) {
                EmailVerification verification = emailVerificationRepository
                                .findByEmailAndOtpCodeAndVerifiedFalseAndExpiresAtAfter(
                                                email,
                                                otpCode,
                                                LocalDateTime.now())
                                .orElse(null);

                if (verification == null) {
                        return false;
                }

                verification.setVerified(true);
                verification.setVerifiedAt(LocalDateTime.now());
                emailVerificationRepository.save(verification);

                return true;
        }

        // Register with email verification
        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("Email đã được sử dụng");
                }

                // Check if email is verified recently (within 30 minutes)
                boolean isVerified = emailVerificationRepository
                                .findTopByEmailAndVerifiedTrueAndVerifiedAtAfterOrderByVerifiedAtDesc(
                                                request.getEmail(),
                                                LocalDateTime.now().minusMinutes(30))
                                .isPresent();

                User user = User.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .phoneNumber(request.getPhoneNumber())
                                .role(request.getRole() != null ? request.getRole() : User.Role.CUSTOMER)
                                .status(User.Status.ACTIVE)
                                .emailVerified(isVerified)
                                .build();

                userRepository.save(user);

                CustomUserDetails userDetails = new CustomUserDetails(user);
                String token = jwtUtils.generateToken(userDetails, user.getUserId(), user.getRole().name());

                return AuthResponse.builder()
                                .token(token)
                                .userId(user.getUserId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build();
        }

        // Login
        public AuthResponse login(AuthRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                User user = userRepository.findById(userDetails.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String token = jwtUtils.generateToken(userDetails, user.getUserId(), user.getRole().name());

                return AuthResponse.builder()
                                .token(token)
                                .userId(user.getUserId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build();
        }

        // Social Login (Google, Facebook)
        @Transactional
        public AuthResponse socialLogin(SocialLoginRequest request) {
                // Check if user exists by email
                User user = userRepository.findByEmail(request.getEmail()).orElse(null);

                if (user == null) {
                        // Create new user from social profile
                        user = User.builder()
                                        .fullName(request.getFullName())
                                        .email(request.getEmail())
                                        .role(User.Role.CUSTOMER)
                                        .status(User.Status.ACTIVE)
                                        .emailVerified(true) // Social accounts are pre-verified
                                        .socialProvider(request.getProvider())
                                        .socialProviderId(request.getProviderId())
                                        .build();

                        userRepository.save(user);
                } else {
                        // Update social provider info if not set
                        if (user.getSocialProvider() == null) {
                                user.setSocialProvider(request.getProvider());
                                user.setSocialProviderId(request.getProviderId());
                                user.setEmailVerified(true);
                                userRepository.save(user);
                        }
                }

                CustomUserDetails userDetails = new CustomUserDetails(user);
                String token = jwtUtils.generateToken(userDetails, user.getUserId(), user.getRole().name());

                return AuthResponse.builder()
                                .token(token)
                                .userId(user.getUserId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build();
        }

        @Transactional
        public void resetPassword(String email, String otpCode, String newPassword) {
                // Verify OTP
                EmailVerification verification = emailVerificationRepository
                                .findByEmailAndOtpCodeAndVerifiedFalseAndExpiresAtAfter(
                                                email,
                                                otpCode,
                                                LocalDateTime.now())
                                .orElseThrow(() -> new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn"));

                // Fetch User
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

                // Update Password
                user.setPasswordHash(passwordEncoder.encode(newPassword));
                userRepository.save(user);

                // Mark OTP as verified
                verification.setVerified(true);
                verification.setVerifiedAt(LocalDateTime.now());
                emailVerificationRepository.save(verification);
        }
}
