package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entitiy.Product;
import com.example.demo.entitiy.Review;
import com.example.demo.entitiy.Users;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repos.ProductRepostory;
import com.example.demo.repos.ReviewRepository;
import com.example.demo.repos.UsersRepository;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepostory productRepository;

    @Autowired
    private UsersRepository usersRepository;

    // Ürünün yorumlarını getir
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<Review>>> getProductReviews(
            @PathVariable Long productId) {
        List<Review> reviews = reviewRepository
                .findByProductIdOrderByCreatedAtDesc(productId);
        return ResponseEntity.ok(ApiResponse.success("Yorumlar listelendi", reviews));
    }

    // Yorum ekle
    @PostMapping
    public ResponseEntity<ApiResponse<Review>> addReview(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer rating,
            @RequestParam String comment) {

        if (rating < 1 || rating > 5) {
            throw new InvalidRequestException("Puan 1-5 arasında olmalıdır");
        }

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(ApiResponse.success("Yorum eklendi", saved));
    }

    // Yorum sil
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewRepository.deleteById(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Yorum silindi", null));
    }
}