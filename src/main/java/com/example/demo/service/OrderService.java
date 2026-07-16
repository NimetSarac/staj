package com.example.demo.service;

import java.time.LocalDateTime;

import com.example.demo.entitiy.Cart;
import com.example.demo.entitiy.CartItem;
import com.example.demo.entitiy.Orders;
import com.example.demo.entitiy.Payment;
import com.example.demo.entitiy.Product;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repos.CartItemRepository;
import com.example.demo.repos.CartRepository;
import com.example.demo.repos.OrdersRepostory;
import com.example.demo.repos.PaymentRepostory;
import com.example.demo.repos.ProductRepostory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrdersRepostory orderrRepository;

    @Autowired
    private PaymentRepostory paymentRepository;

    @Autowired
    private ProductRepostory productRepository;

    @Transactional
    public Payment createOrderFromCart(Long userId, String fullname, String bankName) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Sepet bulunamadı"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Sepet boş, sipariş oluşturulamaz");
        }

        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                    "Yetersiz stok: " + product.getName() +
                    " (mevcut: " + product.getStock() + ")"
                );
            }
        }

        Payment payment = new Payment();
        payment.setUser(cart.getUser());
        payment.setFullname(fullname);
        payment.setBankName(bankName);
        payment.setStatus(false);

        double totalAmount = 0.0;

        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();

            Orders order = new Orders();
            order.setProduct(product);
            order.setQuantity(item.getQuantity());
            order.setUser(cart.getUser());
            order.setOrderStatus(false);
            order.setOrderDate(LocalDateTime.now()); // sipariş anının tarihi/saati

            double lineTotal = product.getPrice() * item.getQuantity();
            order.setPriceTotal(lineTotal);
            order.setPriceDiscount(product.getDiscount());

            payment.addOrder(order);

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            totalAmount += lineTotal;
        }

        payment.setAmount(totalAmount);

        Payment savedPayment = paymentRepository.save(payment);

        cartItemRepository.deleteByCartId(cart.getId());

        return savedPayment;
    }

    @Transactional
    public Payment processFakePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ödeme bulunamadı"));

        if (Boolean.TRUE.equals(payment.getStatus())) {
            throw new InvalidRequestException("Bu ödeme zaten işlenmiş");
        }

        // Her sipariş satırı için stoku düş
        for (Orders order : payment.getOrders()) {

       order.setOrderStatus(true);       }

        return paymentRepository.save(payment);
    }
}