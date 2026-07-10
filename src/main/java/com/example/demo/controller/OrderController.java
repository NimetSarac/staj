package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OrderRequestDto;
import com.example.demo.entitiy.Payment;
import com.example.demo.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Sepetten sipariş oluştur - tek bir Payment, içinde birden fazla Orders
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Payment>> checkout(
            @Valid @RequestBody OrderRequestDto dto) {
        Payment payment = orderService.createOrderFromCart(
                dto.getUserId(),
                dto.getFullname(),
                dto.getBankName()
        );
        return ResponseEntity.ok(ApiResponse.success("Sipariş oluşturuldu", payment));
    }

    // Sahte ödeme işlemini tetikle
    @PostMapping("/{paymentId}/pay")
    public Payment pay(@PathVariable Long paymentId) {
        return orderService.processFakePayment(paymentId);
    }
}