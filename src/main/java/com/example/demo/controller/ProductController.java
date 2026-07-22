package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.ProductMapper;
import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.entitiy.Product;
import com.example.demo.entitiy.ProductImage;
import com.example.demo.repos.ProductImageRepository;
import com.example.demo.service.FileUploadService;
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
    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ProductImageRepository productImageRepository;

    // Çoklu resim yükle
    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<List<String>>> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files) {

        Product product = productService.getProductById(id);

        try {
            List<String> urls = fileUploadService.uploadFiles(files);
            List<ProductImage> images = new ArrayList<>();

            boolean isFirst = productImageRepository.findByProductId(id).isEmpty();

            for (int i = 0; i < urls.size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(urls.get(i));
                image.setPrimary(isFirst && i == 0); // İlk resim primary olsun
                images.add(image);
            }

            productImageRepository.saveAll(images);

            return ResponseEntity.ok(ApiResponse.success("Resimler yüklendi", urls));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Yükleme hatası: " + e.getMessage(), null));
        }
    }

    // Ürünün resimlerini getir
    @GetMapping("/{id}/images")
    public ResponseEntity<ApiResponse<List<ProductImage>>> getImages(@PathVariable Long id) {
        List<ProductImage> images = productImageRepository.findByProductId(id);
        return ResponseEntity.ok(ApiResponse.success("Resimler listelendi", images));
    }

    // Resim sil
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        productImageRepository.deleteById(imageId);
        return ResponseEntity.ok(ApiResponse.success("Resim silindi", null));
    }
}