package com.example.demo.repos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entitiy.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}