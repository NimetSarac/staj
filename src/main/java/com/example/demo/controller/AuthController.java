package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<Users>> register(@RequestParam String username,
                                                        @RequestParam String email,
                                                        @RequestParam String password) {
        Users user = userService.register(username, email, password);
        return ResponseEntity.ok(ApiResponse.success("Kayıt başarılı", user));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Users>> login(@RequestParam String username,
                                                     @RequestParam String password,
                                                     HttpServletRequest request) {
        try {
            Users user = userService.login(username, password);

            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());

            return ResponseEntity.ok(ApiResponse.success("Giriş başarılı", user));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Kullanıcı adı veya şifre hatalı.", null));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);//session yoksa yeni oluştur
        if (session != null) {
            session.invalidate(); //session tamamen geçersil kıl
        }
        return ResponseEntity.ok(ApiResponse.success("Çıkış başarılı", null));
    }    

    // Frontend'in "şu an giriş yapmış kullanıcı kim?" diye sorabilmesi için
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.ok(ApiResponse.error("Giriş yapılmamış", null));
        }
        Long userId = (Long) session.getAttribute("userId");
        Users user = userService.findById(userId);
        return ResponseEntity.ok(ApiResponse.success("Kullanıcı bilgisi", user));
    }

}