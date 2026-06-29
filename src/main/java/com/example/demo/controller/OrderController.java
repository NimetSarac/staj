package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entitiy.Payment;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Sepetten sipariş oluştur - tek bir Payment, içinde birden fazla Orders
    @PostMapping("/checkout")
    public Payment checkout(@RequestParam Long userId,
                             @RequestParam String fullname,
                             @RequestParam String bankName) {
        return orderService.createOrderFromCart(userId, fullname, bankName);
    }

    // Sahte ödeme işlemini tetikle
    @PostMapping("/{paymentId}/pay")
    public Payment pay(@PathVariable Long paymentId) {
        return orderService.processFakePayment(paymentId);
    }
}