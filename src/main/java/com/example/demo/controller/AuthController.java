package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entitiy.Users;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Users register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password) {
        return userService.register(username, email, password);
    }

    @PostMapping("/login")
    public Users login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request) {

        Users user = userService.login(username, password);

        // Giriş başarılı oldu, session'a kullanıcı bilgilerini yaz
        HttpSession session = request.getSession();
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());

        return user;
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // session yoksa yeni oluşturma
        if (session != null) {
            session.invalidate(); // session'ı tamamen geçersiz kıl
        }
    }

    // Frontend'in "şu an giriş yapmış kullanıcı kim?" diye sorabilmesi için
    @GetMapping("/me")
    public Object getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            return null; // giriş yapılmamış
        }

        Long userId = (Long) session.getAttribute("userId");
        return userService.findById(userId);
    }
}