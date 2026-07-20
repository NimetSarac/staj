package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entitiy.PasswordResetToken;
import com.example.demo.entitiy.Users;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repos.PasswordResetTokenRepository;
import com.example.demo.repos.UsersRepository;

@Service
public class PasswordResetService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Şifre sıfırlama isteği — e-posta gönder
    @Transactional
    public void requestPasswordReset(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Bu e-posta ile kayıtlı kullanıcı bulunamadı"
                ));

        // Eski token'ları sil
        tokenRepository.deleteByUserId(user.getId());

        // 6 haneli kod üret
        String token = String.format("%06d", (int)(Math.random() * 1000000));

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // 15 dakika
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        // E-posta gönder
        emailService.sendPasswordResetEmail(email, token);
    }

    // 2. Kodu doğrula
    public boolean verifyToken(String email, String token) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRequestException("Geçersiz kod"));

        if (!resetToken.getUser().getId().equals(user.getId())) {
            throw new InvalidRequestException("Geçersiz kod");
        }

        if (resetToken.isExpired()) {
            throw new InvalidRequestException("Kodun süresi dolmuş. Lütfen yeni kod isteyin.");
        }

        if (resetToken.isUsed()) {
            throw new InvalidRequestException("Bu kod daha önce kullanılmış.");
        }

        return true;
    }

    // 3. Yeni şifre belirle
    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        // Önce token'ı doğrula
        verifyToken(email, token);

        if (newPassword.length() < 6) {
            throw new InvalidRequestException("Şifre en az 6 karakter olmalıdır");
        }

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        // Şifreyi güncelle
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Token'ı kullanıldı olarak işaretle
        PasswordResetToken resetToken = tokenRepository.findByToken(token).get();
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}