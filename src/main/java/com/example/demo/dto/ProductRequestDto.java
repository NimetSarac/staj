package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotBlank(message = "Ürün adı boş olamaz")
    private String name;

    private String image;

    @NotNull(message = "Stok bilgisi zorunludur")
    @PositiveOrZero(message = "Stok 0'dan küçük olamaz")
    private Integer stock;

    @NotNull(message = "Fiyat zorunludur")
    @Positive(message = "Fiyat 0'dan büyük olmalıdır")
    private Double price;

    @PositiveOrZero(message = "İndirim negatif olamaz")
    private Double discount;

    private Boolean status;

    @NotNull(message = "Kategori seçimi zorunludur")
    private Long categoryId;
}