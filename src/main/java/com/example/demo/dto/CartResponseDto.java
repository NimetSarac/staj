package com.example.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {

    private Long id;
    private Long userId;
    private String username;
    private List<CartItemResponseDto> items;
    private Double totalAmount;
}