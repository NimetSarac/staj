package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CartMapper;
import com.example.demo.dto.CartResponseDto;
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

import jakarta.transaction.Transactional;

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

	// Dışarıya CartResponseDto döndürür (Controller için)
	public CartResponseDto getCartByUserId(Long userId) {
		Cart cart = getCartEntityByUserId(userId);
		return CartMapper.toResponseDto(cart);
	}

	// İç kullanım için Cart entity döndürür
	private Cart getCartEntityByUserId(Long userId) {
		return cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Sepet bulunamadı"));
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

	public CartResponseDto addItemToCart(Long userId, Long productId, Integer quantity) {

		if (quantity == null || quantity <= 0) {
			throw new InvalidRequestException("Ürün miktarı 0'dan büyük olmalıdır");
		}

		Cart cart = getOrCreateCart(userId);

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + productId));

		if (product.getStock() < quantity) {
			throw new InvalidRequestException("Yetersiz stok. Mevcut stok: " + product.getStock());
		}

		Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

		if (existingItem.isPresent()) {
			CartItem item = existingItem.get();
			int newQuantity = item.getQuantity() + quantity;

			if (product.getStock() < newQuantity) {
				throw new InvalidRequestException("Yetersiz stok. Sepette zaten " + item.getQuantity()
						+ " adet var, mevcut stok: " + product.getStock());
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

		return CartMapper.toResponseDto(getOrCreateCart(userId));
	}

	public CartResponseDto updateItemQuantity(Long userId, Long cartItemId, Integer newQuantity) {
		getCartEntityByUserId(userId); // sepetin var olduğunu doğrula
		CartItem item = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Sepet ürünü bulunamadı"));

		if (newQuantity == null || newQuantity < 0) {
			throw new InvalidRequestException("Miktar 0 veya daha büyük olmalıdır");
		}

		if (newQuantity == 0) {
			cartItemRepository.delete(item);
		} else {
			if (item.getProduct().getStock() < newQuantity) {
				throw new InvalidRequestException("Yetersiz stok. Mevcut stok: " + item.getProduct().getStock());
			}
			item.setQuantity(newQuantity);
			cartItemRepository.save(item);
		}

		return getCartByUserId(userId);
	}

	public void removeItemFromCart(Long userId, Long cartItemId) {
		// Kullanıcının sepeti var mı kontrol et
		getCartEntityByUserId(userId);

		// CartItem var mı kontrol et
		CartItem item = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Sepet ürünü bulunamadı: " + cartItemId));

		// CartItem bu kullanıcının sepetine mi ait?
		Cart cart = getCartEntityByUserId(userId);
		if (!item.getCart().getId().equals(cart.getId())) {
			throw new InvalidRequestException("Bu ürün sizin sepetinizde değil");
		}

		cartItemRepository.delete(item); 
	}
	@Transactional
     public void clearCart(Long userId) {
		Cart cart = getCartEntityByUserId(userId); // deleteByCartId() Spring Data JPA'nın türetilmiş bir silme metodu.
		                                           //Bu tür metodlar aktif bir transaction içinde çalışması gerekiyor
		cartItemRepository.deleteByCartId(cart.getId());
	}

	public Double calculateCartTotal(Long userId) {
		Cart cart = getCartEntityByUserId(userId); // entity lazım
		return cart.getCartItems().stream().mapToDouble(CartItem::getSubtotal).sum();
	}
}