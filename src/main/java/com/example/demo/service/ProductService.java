package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitiy.Category;
import com.example.demo.entitiy.Product;
import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.dto.ProductMapper;
import com.example.demo.repos.CategoryRepostory;
import com.example.demo.repos.ProductRepostory;

@Service
public class ProductService {

    @Autowired
    private ProductRepostory productRepository;

    @Autowired
    private CategoryRepostory categoryRepository;


    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));
    }

    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // DTO kullanan yeni create metodu
    public Product create(ProductRequestDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + dto.getCategoryId()));

        Product product = ProductMapper.toEntity(dto);
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