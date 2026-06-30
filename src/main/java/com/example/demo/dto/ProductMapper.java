package com.example.demo.dto;

import com.example.demo.entitiy.Product;

public class ProductMapper {

    public static ProductResponseDto toResponseDto(Product product) {
        if (product == null) {
            return null;
        }

        Long categoryId = null;
        String categoryName = null;

        if (product.getCategory() != null) {
            categoryId = product.getCategory().getId();
            categoryName = product.getCategory().getName();
        }

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getImage(),
                product.getStock(),
                product.getPrice(),
                product.getDiscount(),
                product.getStatus(),
                categoryId,
                categoryName
        );
    }

    public static Product toEntity(ProductRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setImage(dto.getImage());
        product.setStock(dto.getStock());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount());
        product.setStatus(dto.getStatus());

        return product;
    }
}