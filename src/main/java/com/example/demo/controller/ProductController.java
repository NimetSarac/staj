package com.example.demo.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.ProductMapper;
import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.entitiy.Product;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAll() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Ürünler listelendi", products));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Ürün bulundu", ProductMapper.toResponseDto(product)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getByCategory(
            @PathVariable Long categoryId) {
        List<ProductResponseDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Kategoriye göre ürünler", products));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> search(
            @RequestParam String keyword) {
        List<ProductResponseDto> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success("Arama tamamlandı", products));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getActiveProducts() {
        List<ProductResponseDto> products = productService.getActiveProducts();
        return ResponseEntity.ok(ApiResponse.success("Aktif ürünler listelendi", products));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponseDto>>> getAllPaged(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        PageResponse<ProductResponseDto> result = productService.getAllProductsPaged(pageable);
        return ResponseEntity.ok(ApiResponse.success("Ürünler sayfalı listelendi", result));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> filterProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long categoryId) {
        List<ProductResponseDto> result = productService.filterProducts(minPrice, maxPrice, categoryId);
        return ResponseEntity.ok(ApiResponse.success("Filtrelendi", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> create(
            @RequestBody ProductRequestDto dto) {
        Product saved = productService.create(dto);
        return new ResponseEntity<>(
            ApiResponse.success("Ürün oluşturuldu", ProductMapper.toResponseDto(saved)),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> update(
            @PathVariable Long id,
            @RequestBody Product updatedProduct) {
        Product updated = productService.updateProduct(id, updatedProduct);
        return ResponseEntity.ok(ApiResponse.success("Ürün güncellendi", ProductMapper.toResponseDto(updated)));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Ürün silindi", null));
    }
}