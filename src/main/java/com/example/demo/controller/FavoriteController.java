package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductMapper;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.entitiy.Favorite;
import com.example.demo.entitiy.Product;
import com.example.demo.entitiy.Users;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repos.FavoriteRepository;
import com.example.demo.repos.ProductRepostory;
import com.example.demo.repos.UsersRepository;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepostory productRepository;

    @Autowired
    private UsersRepository usersRepository;

    // Kullanıcının favori ürünlerini listele
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getFavorites(
            @PathVariable Long userId) {

        List<ProductResponseDto> favorites = favoriteRepository
                .findByUserId(userId)
                .stream()
                .map(f -> ProductMapper.toResponseDto(f.getProduct()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Favoriler listelendi", favorites));
    }

    // Favori ekle
    @PostMapping("/{userId}/{productId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            return ResponseEntity.ok(ApiResponse.success("Zaten favorilerde", null));
        }

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);

        return ResponseEntity.ok(ApiResponse.success("Favorilere eklendi", null));
    }

    // Favoriden çıkar
    @DeleteMapping("/{userId}/{productId}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Favorilerden çıkarıldı", null));
    }

    // Ürün favoride mi?
    @GetMapping("/{userId}/{productId}/check")
    public ResponseEntity<ApiResponse<Boolean>> checkFavorite(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        boolean isFavorite = favoriteRepository.existsByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Kontrol edildi", isFavorite));
    }
}