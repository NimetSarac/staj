package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitiy.Cart;
import com.example.demo.entitiy.CartItem;
import com.example.demo.entitiy.Product;
import com.example.demo.entitiy.Users;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repos.CartItemRepository;
import com.example.demo.repos.CartRepository;
import com.example.demo.repos.ProductRepostory;
import com.example.demo.repos.UsersRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepostory productRepository;

    @Autowired
    private UsersRepository usersRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sepet bulunamadı"));
    }

    private Cart getOrCreateCart(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));

        Optional<Cart> existingCart = cartRepository.findByUserId(userId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }

    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + productId));

        // RuntimeException → InvalidRequestException
        if (product.getStock() < quantity) {
            throw new InvalidRequestException("Yetersiz stok. Mevcut stok: " + product.getStock());
        }

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            // RuntimeException → InvalidRequestException
            if (product.getStock() < newQuantity) {
                throw new InvalidRequestException("Yetersiz stok. Mevcut stok: " + product.getStock());
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        return getOrCreateCart(userId);
    }

    public Cart updateItemQuantity(Long userId, Long cartItemId, Integer newQuantity) {
        getCartByUserId(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sepet ürünü bulunamadı"));

        if (newQuantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            // RuntimeException → InvalidRequestException
            if (item.getProduct().getStock() < newQuantity) {
                throw new InvalidRequestException("Yetersiz stok");
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

    public Double calculateCartTotal(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getCartItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
}