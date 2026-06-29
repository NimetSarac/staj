package com.example.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entitiy.Payment;

public interface PaymentRepostory extends JpaRepository<Payment, Long> {

}
