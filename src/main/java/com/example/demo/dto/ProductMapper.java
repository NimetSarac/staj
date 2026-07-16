package com.example.demo.dto;

import com.example.demo.entitiy.Product;

public class ProductMapper {

	public static ProductResponseDto toResponseDto(Product product) {
	    if (product == null) return null;

	    CategorySummaryDto categoryDto = null;
	    if (product.getCategory() != null) {
	        categoryDto = new CategorySummaryDto(
	                product.getCategory().getId(),
	                product.getCategory().getName()
	        );
	    }

	    return new ProductResponseDto(
	            product.getId(),
	            product.getName(),
	            product.getImage(),
	            product.getStock() != null ? product.getStock() : 0,
	            product.getPrice() != null ? product.getPrice() : 0.0,
	            product.getDiscount() != null ? product.getDiscount() : 0.0,
	            product.getStatus() != null ? product.getStatus() : Boolean.TRUE,
	            product.getDescription(),
	            categoryDto
	    );
	}

    public static Product toEntity(ProductRequestDto dto) {
        if (dto == null) return null;

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