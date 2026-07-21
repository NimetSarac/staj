package com.example.demo.service;

import com.example.demo.entitiy.Cart;
import com.example.demo.entitiy.Users;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repos.CartItemRepository;
import com.example.demo.repos.CartRepository;
import com.example.demo.repos.FavoriteRepository;
import com.example.demo.repos.UsersRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private final UsersRepository userRepository;

	@Autowired
	private final CartRepository cartRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private FavoriteRepository favoriteRepository;
	
	@Autowired
	private EmailService emailService;

	public UserService(UsersRepository userRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.passwordEncoder = passwordEncoder;
	}

	

	public Users register(String username, String email, String rawPassword) {

	    if (userRepository.existsByUsername(username)) {
	        throw new InvalidRequestException("Bu kullanıcı adı zaten kullanılıyor");
	    }
	    if (userRepository.existsByEmail(email)) {
	        throw new InvalidRequestException("Bu e-posta zaten kayıtlı");
	    }

	    // 6 haneli doğrulama kodu üret
	    String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));

	    Users user = new Users();
	    user.setUsername(username);
	    user.setEmail(email);
	    user.setPassword(passwordEncoder.encode(rawPassword));
	    user.setStatus(true);
	    user.setRole("CUSTOMER");
	    user.setEmailVerified(false); // Henüz doğrulanmamış
	    user.setVerificationCode(verificationCode);
	    user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));

	    Users savedUser = userRepository.save(user);

	    // Boş sepet oluştur
	    Cart cart = new Cart();
	    cart.setUser(savedUser);
	    cartRepository.save(cart);

	    // Doğrulama maili gönder
	    emailService.sendVerificationEmail(email, verificationCode);

	    return savedUser;
	}

	public Users login(String email, String rawPassword) {
	    Users user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

	    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
	        throw new RuntimeException("Şifre yanlış");
	    }

	    // E-posta doğrulanmamışsa giriş yapılamaz
	    if (!user.isEmailVerified()) {
	        throw new InvalidRequestException("E-posta adresiniz doğrulanmamış. Lütfen e-postanızı kontrol edin.");
	    }

	    return user;
	}

	public Users findByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
	}

	public Users findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
	}

	public void changePassword(Long userId, String currentPassword, String newPassword) {
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

		// Mevcut şifre doğru mu kontrol et
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new InvalidRequestException("Mevcut şifre hatalı");
		}

		// Yeni şifre en az 6 karakter olmalı
		if (newPassword.length() < 6) {
			throw new InvalidRequestException("Yeni şifre en az 6 karakter olmalıdır");
		}

		// Yeni şifreyi hashle ve kaydet
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	@Transactional
	public void deleteUser(Long userId) {
		// 1. Favori kayıtlarını sil
		favoriteRepository.deleteByUserId(userId);

		// 2. Sepet ürünlerini sil
		cartRepository.findByUserId(userId).ifPresent(cart -> {
			cartItemRepository.deleteByCartId(cart.getId());
			cartRepository.delete(cart);
		});

		// 3. Kullanıcıyı sil
		userRepository.deleteById(userId);
	}
	@Transactional
	public void verifyEmail(String email, String code) {
	    Users user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

	    if (user.isEmailVerified()) {
	        throw new InvalidRequestException("E-posta zaten doğrulanmış");
	    }

	    if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
	        throw new InvalidRequestException("Geçersiz doğrulama kodu");
	    }

	    if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiry())) {
	        throw new InvalidRequestException("Doğrulama kodunun süresi dolmuş");
	    }

	    user.setEmailVerified(true);
	    user.setVerificationCode(null);
	    user.setVerificationCodeExpiry(null);
	    userRepository.save(user);
	}

	// Kodu yeniden gönder
	@Transactional
	public void resendVerificationCode(String email) {
	    Users user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

	    if (user.isEmailVerified()) {
	        throw new InvalidRequestException("E-posta zaten doğrulanmış");
	    }

	    String code = String.format("%06d", (int)(Math.random() * 1000000));
	    user.setVerificationCode(code);
	    user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));
	    userRepository.save(user);

	    emailService.sendVerificationEmail(email, code);
	}



}