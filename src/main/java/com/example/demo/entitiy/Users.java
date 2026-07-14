package com.example.demo.entitiy;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	private String fullname;

	@Column(name = "phone_number")
	private String phoneNumber;

	private String email;

	private String password;

	private Boolean status;

	private String role;
	private String address;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<Payment> payments = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<Orders> orders = new ArrayList<>();

	public Users() {
	}
}