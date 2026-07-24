package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OrderRequestDto;
import com.example.demo.entitiy.Orders;
import com.example.demo.entitiy.Payment;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repos.OrdersRepostory;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;

@RestController


@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @Autowired
    private OrdersRepostory orderrRepository;
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Orders>>> getOrdersByUser(@PathVariable Long userId) {
        List<Orders> orders = orderrRepository.findByUserIdOrderByOrderDateDesc(userId);
        return ResponseEntity.ok(ApiResponse.success("Siparişler listelendi", orders));
    }

    // Sahte ödeme işlemini tetikle
    @PostMapping("/{paymentId}/pay")
    public Payment pay(@PathVariable Long paymentId) {
        return orderService.processFakePayment(paymentId);
    }
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Orders>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Boolean status) {
        Orders order = orderrRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı"));
        order.setOrderStatus(status);
        orderrRepository.save(order);
        return ResponseEntity.ok(ApiResponse.success("Sipariş durumu güncellendi", order));
    }
    @PutMapping("/{orderId}/cargo")
    public ResponseEntity<ApiResponse<Orders>> updateCargoStatus(
            @PathVariable Long orderId,
            @RequestParam String cargoStatus,
            @RequestParam(required = false) String cargoTrackingNumber,
            @RequestParam(required = false) String cargoCompany) {

        Orders order = orderrRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı"));

        order.setCargoStatus(cargoStatus);
        if (cargoTrackingNumber != null) order.setCargoTrackingNumber(cargoTrackingNumber);
        if (cargoCompany != null) order.setCargoCompany(cargoCompany);

        orderrRepository.save(order);
        return ResponseEntity.ok(ApiResponse.success("Kargo durumu güncellendi", order));
    }
}