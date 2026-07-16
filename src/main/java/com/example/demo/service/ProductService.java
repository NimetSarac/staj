package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.ProductMapper;
import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.entitiy.Category;
import com.example.demo.entitiy.Product;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
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
    public PageResponse<ProductResponseDto> getAllProductsPaged(Pageable pageable) {

        // 1. Veritabanından sayfalı veri çek
        Page<Product> page = productRepository.findAll(pageable);

        // 2. Her Product'ı ProductResponseDto'ya çevir
        List<ProductResponseDto> content = page.getContent()
                .stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());

        // 3. PageResponse oluştur ve döndür
        return new PageResponse<>(
                content,
                page.getNumber(),         // currentPage
                page.getTotalPages(),     // totalPages
                page.getTotalElements(),  // totalElements
                page.getSize(),           // pageSize
                page.isFirst(),           // isFirst
                page.isLast()             // isLast
        );
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));
    }

    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {

        // Kategori var mı doğrula
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Kategori bulunamadı: " + categoryId
                ));

        return productRepository.findByCategory_Id(categoryId)
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

    public Product create(ProductRequestDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Kategori bulunamadı: " + dto.getCategoryId()
                ));

        // Aynı isimde ürün var mı kontrol et
        boolean isimZatenVar = productRepository
                .findByNameContainingIgnoreCase(dto.getName())
                .stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(dto.getName()));

        if (isimZatenVar) {
            throw new InvalidRequestException(
                "Bu isimde bir ürün zaten mevcut: " + dto.getName()
            );
        }

        Product product = ProductMapper.toEntity(dto);
        product.setCategory(category);

        return productRepository.save(product);
    }

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
        getProductById(id);
        productRepository.deleteById(id);
    }

    // Sadece aktif ü rünleri listele
    public List<ProductResponseDto> getActiveProducts() {
        return productRepository.findByStatus(true)
                .stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public List<ProductResponseDto> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
}
    public List<ProductResponseDto> filterProducts(Double minPrice, Double maxPrice, Long categoryId) {
        List<Product> products;

        if (categoryId != null && minPrice != null && maxPrice != null) {
            products = productRepository.findByCategory_IdAndPriceBetween(categoryId, minPrice, maxPrice);
        } else if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}