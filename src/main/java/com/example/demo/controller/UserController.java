package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entitiy.Users;
import com.example.demo.repos.UsersRepository;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping("/api/users")
public class UserController {
	private UsersRepository userRepository;

	public UserController(UsersRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public List<Users> getAll() {
		return userRepository.findAll();
	}

	@GetMapping("/{id}")
	public Users getById(@PathVariable Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
	}

	@PostMapping
	public Users create(@RequestBody Users user) {
		return userRepository.save(user);
	}

	 @PutMapping("/{id}")
	    public Users update(@PathVariable Long id, @RequestBody Users updatedUser) {
	        Users existing = userRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));

	        existing.setFullname(updatedUser.getFullname());
	        existing.setEmail(updatedUser.getEmail());
	        existing.setPhoneNumber(updatedUser.getPhoneNumber());

	        return userRepository.save(existing);
	 }
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		userRepository.deleteById(id);
	}
}
