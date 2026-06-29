package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitiy.Cart;
import com.example.demo.entitiy.CartItem;
import com.example.demo.entitiy.Product;
import com.example.demo.repos.CartItemRepository;
import com.example.demo.repos.CartRepository;
import com.example.demo.repos.ProductRepostory;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepostory productRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Sepet bulunamadı"));
    }

    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        // Stok kontrolü
        if (product.getStock() < quantity) {
            throw new RuntimeException("Yetersiz stok. Mevcut stok: " + product.getStock());
        }

        // Ürün zaten sepette mi diye kontrol et
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            // Varsa miktarı güncelle
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Yetersiz stok. Mevcut stok: " + product.getStock());
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Yoksa yeni bir CartItem oluştur
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        return getCartByUserId(userId);
    }

    public Cart updateItemQuantity(Long userId, Long cartItemId, Integer newQuantity) {
        getCartByUserId(userId); // sepetin var olduğunu doğrula
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Sepet ürünü bulunamadı"));

        if (newQuantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            if (item.getProduct().getStock() < newQuantity) {
                throw new RuntimeException("Yetersiz stok");
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        }

        return getCartByUserId(userId);
    }

    public void removeItemFromCart(Long userId, Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    // Sepetin toplam tutarını hesapla
    public Double calculateCartTotal(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getCartItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
}