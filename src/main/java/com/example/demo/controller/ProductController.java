package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entitiy.Product;
import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.dto.ProductMapper;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponseDto> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponseDto getById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ProductMapper.toResponseDto(product);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> getByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/search")
    public List<ProductResponseDto> search(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    @PostMapping
    public ProductResponseDto create(@RequestBody ProductRequestDto dto) {
        Product saved = productService.create(dto);
        return ProductMapper.toResponseDto(saved);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product updated = productService.updateProduct(id, updatedProduct);
        return ProductMapper.toResponseDto(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}