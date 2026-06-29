package com.example.demo.service;

import com.example.demo.entitiy.Cart;
import com.example.demo.entitiy.Users;
import com.example.demo.repos.CartRepository;
import com.example.demo.repos.UsersRepository;

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

        Users savedUser = userRepository.save(user);

        // Kullanıcı oluşturulunca otomatik olarak boş bir sepet de oluşturuyoruz
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        return savedUser;
    }

    public Users login(String username, String rawPassword) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Şifre yanlış");
        }

        return user;
    }

    public Users findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }

    public Users findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }
}