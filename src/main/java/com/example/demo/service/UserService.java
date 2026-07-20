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

	public UserService(UsersRepository userRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Users register(String username, String email, String rawPassword) {

		if (userRepository.existsByUsername(username)) {
			throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
		}
		if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("Bu e-posta zaten kayıtlı");
		}

		Users user = new Users();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setStatus(true);
		user.setRole("CUSTOMER");
		Users savedUser = userRepository.save(user);

		// Kullanıcı oluşturulunca otomatik olarak boş bir sepet de oluşturuyoruz
		Cart cart = new Cart();
		cart.setUser(savedUser);
		cartRepository.save(cart);

		return savedUser;
	}

	public Users login(String email, String rawPassword) {
		Users user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

		if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
			throw new RuntimeException("Şifre yanlış");
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
}