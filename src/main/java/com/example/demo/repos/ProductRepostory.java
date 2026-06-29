package com.example.demo.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entitiy.Product;

public interface ProductRepostory extends JpaRepository<Product, Long> {
	List<Product> findByCategoryId(Long categoryId);

	// İsme göre arama yapmak için (büyük/küçük harf duyarsız)
	List<Product> findByNameContainingIgnoreCase(String keyword);

	// Stokta olan ürünleri listelemek için
	List<Product> findByStockGreaterThan(Integer stock);
}
