package org.example.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @org.springframework.scheduling.annotation.Async
    public void sendOtpEmail(String toEmail, String otpCode) {
        logger.info("Attempting to send OTP email to: {}", toEmail);
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = 
                new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Ngayle.com");
            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực đăng ký tài khoản Ngayle.com");
            
            String htmlContent = String.format(
                "<div style='font-family: sans-serif; padding: 20px; color: #333;'>" +
                "<h2>Xác thực tài khoản</h2>" +
                "<p>Xin chào,</p>" +
                "<p>Mã OTP của bạn là: <strong style='font-size: 24px; color: #d32f2f;'>%s</strong></p>" +
                "<p>Mã này sẽ hết hạn sau 5 phút.</p>" +
                "<br/>" +
                "<p>Trân trọng,<br/>Đội ngũ Ngayle.com</p>" +
                "</div>", otpCode);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Successfully sent OTP email to: {}", toEmail);
        } catch (Exception e) {
            logger.error("CRITICAL: Failed to send OTP email to: {}", toEmail);
            logger.error("Error details: ", e);
        }
    }
}
