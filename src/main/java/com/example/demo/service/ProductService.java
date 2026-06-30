package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entitiy.Category;
import com.example.demo.entitiy.Product;
import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductMapper;
import com.example.demo.repos.CategoryRepostory;
import com.example.demo.repos.ProductRepostory;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepostory productRepository;

    @Autowired
    private CategoryRepostory categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // DTO kullanan yeni create metodu
    public Product create(ProductRequestDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + dto.getCategoryId()));

        // Mapper, DTO'daki düz alanları (name, image, stock, price, discount, status) Product'a çeviriyor
        Product product = ProductMapper.toEntity(dto);

        // Mapper category'yi set etmiyordu (hatırlarsan bilerek öyle bırakmıştık)
        // çünkü Category'yi bulmak veritabanı sorgusu gerektiriyor, mapper'ın işi değil
        product.setCategory(category);

        return productRepository.save(product);
    }

    // Admin işlemi: ürün güncelleme
    public Product updateProduct(Long id, Product updatedData) {
        Product existing = getProductById(id);
        existing.setName(updatedData.getName());
        existing.setImage(updatedData.getImage());
        existing.setStock(updatedData.getStock());
        existing.setPrice(updatedData.getPrice());
        existing.setDiscount(updatedData.getDiscount());
        existing.setStatus(updatedData.getStatus());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}