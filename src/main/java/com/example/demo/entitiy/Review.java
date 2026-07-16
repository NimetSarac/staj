package com.example.demo.entitiy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
@Table(name = "review")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnoreProperties({ "password", "cart", "orders" })
	private Users user;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@JsonIgnoreProperties({ "category", "cartItems" })
	private Product product;

	private Integer rating; // 1-5 yıldız

	@Column(columnDefinition = "TEXT")
	private String comment;

	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
	}
}