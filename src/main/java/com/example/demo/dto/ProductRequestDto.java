package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    private String name;
    private String image;
    private Integer stock;
    private Double price;
    private Double discount;
    private Boolean status;
    private Long categoryId;
}