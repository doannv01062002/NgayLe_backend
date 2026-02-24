package org.example.backend.controller;

import org.example.backend.dto.*;
import org.example.backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    // Helper to create response with HttpOnly cookie
    private ResponseEntity<AuthResponse> createCookieResponse(AuthResponse authResponse) {
        String token = authResponse.getToken(); // Capture token

        // Create a new response object with token included for Bearer auth fallback
        AuthResponse safeResponse = new AuthResponse(
                token,
                authResponse.getUserId(),
                authResponse.getFullName(),
                authResponse.getEmail(),
                authResponse.getRole());

        ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(24 * 60 * 60) // 1 day
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(safeResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return createCookieResponse(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return createCookieResponse(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        ResponseCookie cookie = ResponseCookie.from("auth_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã đăng xuất thành công");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody OtpRequest request) {
        authService.sendOtp(request.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mã OTP đã được gửi đến email của bạn");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody OtpVerifyRequest request) {
        boolean verified = authService.verifyOtp(request.getEmail(), request.getOtpCode());
        Map<String, Object> response = new HashMap<>();
        response.put("verified", verified);
        response.put("message", verified ? "Email đã được xác thực" : "Mã OTP không hợp lệ hoặc đã hết hạn");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest request) {
        try {
            AuthResponse response = authService.socialLogin(request);
            logger.info("Social login successful for email: {}", request.getEmail());
            return createCookieResponse(response);
        } catch (Exception e) {
            logger.error("Social login failed for email: {}", request.getEmail(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage() != null ? e.getMessage() : "Đăng nhập thất bại");
            errorResponse.put("type", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<Map<String, String>> forgotPasswordSendOtp(@RequestBody OtpRequest request) {
        authService.sendPasswordResetOtp(request.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mã OTP khôi phục mật khẩu đã được gửi đến email của bạn");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getOtpCode(), request.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mật khẩu đã được đặt lại thành công");
        return ResponseEntity.ok(response);
    }
}
