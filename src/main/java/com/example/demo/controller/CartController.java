package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CartResponseDto;
import com.example.demo.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponseDto>> getCart(@PathVariable Long userId) {
        CartResponseDto cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Sepet getirildi", cart));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<ApiResponse<CartResponseDto>> addItem(@PathVariable Long userId,
                                                                 @RequestParam Long productId,
                                                                 @RequestParam Integer quantity) {
        CartResponseDto cart = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Ürün sepete eklendi", cart));
    }

    @PutMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponseDto>> updateItem(@PathVariable Long userId,
                                                                    @PathVariable Long cartItemId,
                                                                    @RequestParam Integer quantity) {
        CartResponseDto cart = cartService.updateItemQuantity(userId, cartItemId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Sepet güncellendi", cart));
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable Long userId,
                                                         @PathVariable Long cartItemId) {
        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok(ApiResponse.success("Ürün sepetten çıkarıldı", null));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Sepet temizlendi", null));
    }

    @GetMapping("/{userId}/total")
    public ResponseEntity<ApiResponse<Double>> getTotal(@PathVariable Long userId) {
        Double total = cartService.calculateCartTotal(userId);
        return ResponseEntity.ok(ApiResponse.success("Sepet toplamı hesaplandı", total));
    }
}