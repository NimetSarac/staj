package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entitiy.Cart;
import com.example.demo.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId);
    }

    @PostMapping("/{userId}/items")
    public Cart addItem(@PathVariable Long userId,
                         @RequestParam Long productId,
                         @RequestParam Integer quantity) {
        return cartService.addItemToCart(userId, productId, quantity);
    }

    @PutMapping("/{userId}/items/{cartItemId}")
    public Cart updateItem(@PathVariable Long userId,
                            @PathVariable Long cartItemId,
                            @RequestParam Integer quantity) {
        return cartService.updateItemQuantity(userId, cartItemId, quantity);
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public void removeItem(@PathVariable Long userId, @PathVariable Long cartItemId) {
        cartService.removeItemFromCart(userId, cartItemId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
    }

    @GetMapping("/{userId}/total")
    public Double getTotal(@PathVariable Long userId) {
        return cartService.calculateCartTotal(userId);
    }
}