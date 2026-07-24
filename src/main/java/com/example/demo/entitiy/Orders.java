package com.example.demo.entitiy;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pack_price")
    private Double packPrice;

    private String description;

    @Column(name = "price_total")
    private Double priceTotal;

    @Column(name = "price_discount")
    private Double priceDiscount;

    private Integer quantity;

    @Column(name = "order_status")
    private Boolean orderStatus;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    
    
    private String cargoStatus; // HAZIRLANIYOR, KARGOYA_VERILDI, DAGITIMDA, TESLIM_EDILDI
    private String cargoTrackingNumber;
    private String cargoCompany;

    public Orders() {
    }
}