package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Şifre Sıfırlama Talebi");
        message.setText(
            "Merhaba,\n\n" +
            "Şifre sıfırlama talebiniz alındı.\n\n" +
            "Sıfırlama kodunuz: " + token + "\n\n" +
            "Bu kod 15 dakika geçerlidir.\n\n" +
            "Eğer bu talebi siz oluşturmadıysanız, bu e-postayı dikkate almayın.\n\n" +
            "E-Ticaret Sistemi"
        );
        mailSender.send(message);
    }
}