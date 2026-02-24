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
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Mã xác thực đăng ký tài khoản Ngayle.com");
            message.setText(
                    "Xin chào,\n\n" +
                            "Mã OTP của bạn là: " + otpCode + "\n\n" +
                            "Mã này sẽ hết hạn sau 5 phút.\n\n" +
                            "Trân trọng,\n" +
                            "Đội ngũ Ngayle.com");

            mailSender.send(message);
            logger.info("Successfully sent OTP email to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to: {}. Error: {}", toEmail, e.getMessage());
            e.printStackTrace();
        }
    }
}
