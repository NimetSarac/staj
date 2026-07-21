package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.entitiy.Users;
import com.example.demo.service.JwtService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Users>> register(@RequestParam String username, @RequestParam String email,
			@RequestParam String password) {
		Users user = userService.register(username, email, password);
		return ResponseEntity.ok(ApiResponse.success("Kayıt başarılı", user));
	}

	@Autowired
	private JwtService jwtService;
	@Autowired
	private PasswordResetService passwordResetService;


	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponseDto>> login(
	        @RequestParam String email,
	        @RequestParam String password,
	        HttpServletRequest request) {
	    try {
	        Users user = userService.login(email, password);

	        // Session oluştur
	        HttpSession session = request.getSession();
	        session.setAttribute("userId", user.getId());
	        session.setAttribute("role", user.getRole());

	        // JWT token üret
	        String token = jwtService.generateToken(
	                user.getId(),
	                user.getUsername(),
	                user.getRole()
	        );

	        // Sadece güvenli bilgileri döndür — şifre YOK
	        LoginResponseDto responseDto = new LoginResponseDto(
	                token,
	                user.getId(),
	                user.getUsername(),
	                user.getRole()
	        );

	        return ResponseEntity.ok(ApiResponse.success("Giriş başarılı", responseDto));

	    } catch (Exception e) {
	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body(ApiResponse.error("E-posta veya şifre hatalı.", null));
	    }
	}
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);// session yoksa yeni oluştur
		if (session != null) {
			session.invalidate(); // session tamamen geçersil kıl
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
	
	// Şifre sıfırlama isteği
	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
	    passwordResetService.requestPasswordReset(email);
	    return ResponseEntity.ok(ApiResponse.success(
	        "Şifre sıfırlama kodu e-posta adresinize gönderildi.", null
	    ));
	}

	// Kodu doğrula
	@PostMapping("/verify-reset-code")
	public ResponseEntity<ApiResponse<Boolean>> verifyResetCode(
	        @RequestParam String email,
	        @RequestParam String token) {
	    boolean valid = passwordResetService.verifyToken(email, token);
	    return ResponseEntity.ok(ApiResponse.success("Kod doğrulandı", valid));
	}

	// Yeni şifre belirle
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(
	        @RequestParam String email,
	        @RequestParam String token,
	        @RequestParam String newPassword) {
	    passwordResetService.resetPassword(email, token, newPassword);
	    return ResponseEntity.ok(ApiResponse.success("Şifreniz başarıyla güncellendi.", null));
	}
	
	// E-posta doğrula
	@PostMapping("/verify-email")
	public ResponseEntity<ApiResponse<Void>> verifyEmail(
	        @RequestParam String email,
	        @RequestParam String code) {
	    userService.verifyEmail(email, code);
	    return ResponseEntity.ok(ApiResponse.success("E-posta başarıyla doğrulandı", null));
	}

	// Kodu yeniden gönder
	@PostMapping("/resend-verification")
	public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
	    userService.resendVerificationCode(email);
	    return ResponseEntity.ok(ApiResponse.success("Doğrulama kodu yeniden gönderildi", null));
	}

}