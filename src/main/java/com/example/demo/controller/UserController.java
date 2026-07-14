package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entitiy.Users;
import com.example.demo.repos.UsersRepository;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UsersRepository userRepository;
	@Autowired
	private UserService userService;

	public UserController(UsersRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<Users>>> getAll() {
		List<Users> users = userRepository.findAll();
		return ResponseEntity.ok(ApiResponse.success("Kullanıcılar listelendi", users));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Users>> getById(@PathVariable Long id) {
		Users user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
		return ResponseEntity.ok(ApiResponse.success("Kullanıcı bulundu", user));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Users>> create(@RequestBody Users user) {
		Users saved = userRepository.save(user);
		return new ResponseEntity<>(ApiResponse.success("Kullanıcı oluşturuldu", saved), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Users>> update(@PathVariable Long id,
	                                                  @RequestBody Users updatedUser) {
	    Users existing = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));

	    existing.setFullname(updatedUser.getFullname());
	    existing.setEmail(updatedUser.getEmail());
	    existing.setPhoneNumber(updatedUser.getPhoneNumber());
	    existing.setRole(updatedUser.getRole());  // ← BU SATIR EKSİK

	    Users saved = userRepository.save(existing);
	    return ResponseEntity.ok(ApiResponse.success("Kullanıcı güncellendi", saved));
	}
	@PutMapping("/{id}/change-password")
	public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long id, @RequestParam String currentPassword,
			@RequestParam String newPassword) {
		userService.changePassword(id, currentPassword, newPassword);
		return ResponseEntity.ok(ApiResponse.success("Şifre başarıyla değiştirildi", null));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		userRepository.deleteById(id);
		return ResponseEntity.ok(ApiResponse.success("Kullanıcı silindi", null));
	}
}