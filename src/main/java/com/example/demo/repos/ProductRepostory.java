package com.example.demo.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entitiy.Product;

@Repository
public interface ProductRepostory extends JpaRepository<Product, Long> {

    // Kategoriye göre ürünleri listele (alt çizgi ile ilişkili nesnenin alanına erişim)
    List<Product> findByCategory_Id(Long categoryId);

    // ESKİ — bunu sil veya yorum satırına al:
    // List<Product> findByCategoryId(Long categoryId);

    // İsme göre arama
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Stokta olan ürünler
    List<Product> findByStockGreaterThan(Integer stock);

    // Aktif/pasif ürünler
    List<Product> findByStatus(Boolean status);

    // Kategoriye göre aktif ürünler
    List<Product> findByCategory_IdAndStatus(Long categoryId, Boolean status);
}