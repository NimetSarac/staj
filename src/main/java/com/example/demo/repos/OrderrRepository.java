package com.example.demo.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entitiy.Orders;


public interface OrderrRepository extends JpaRepository<Orders, Long> {
	List<Orders> findByUserId(Long userId);

    // En son verilen siparişler önce gelsin
    List<Orders> findByUserIdOrderByOrderDateDesc(Long userId);
}
