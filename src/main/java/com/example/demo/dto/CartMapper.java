package com.example.demo.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.entitiy.Cart;
import com.example.demo.entitiy.CartItem;

public class CartMapper {

    public static CartResponseDto toResponseDto(Cart cart) {
        if (cart == null) return null;

        List<CartItemResponseDto> items = cart.getCartItems()
                .stream()
                .map(CartMapper::toItemResponseDto)
                .collect(Collectors.toList());

        double totalAmount = items.stream()
                .mapToDouble(CartItemResponseDto::getSubtotal)
                .sum();

        return new CartResponseDto(
                cart.getId(),
                cart.getUser().getId(),
                cart.getUser().getUsername(),
                items,
                totalAmount
        );
    }

    public static CartItemResponseDto toItemResponseDto(CartItem item) {
        double subtotal = item.getProduct().getPrice() * item.getQuantity();

        return new CartItemResponseDto(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                subtotal
        );
    }
}