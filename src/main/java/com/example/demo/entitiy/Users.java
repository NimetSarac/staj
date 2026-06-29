package com.example.demo.entitiy;

import java.util.List;

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

	@OneToMany(mappedBy = "user")
	private List<Payment> payments;

	@OneToMany(mappedBy = "user")
	private List<Orders> orders;

	public Users(Long id, String username, String fullname, String phoneNumber, String email, String password,
			Boolean status, List<Payment> payments, List<Orders> orders) {
		super();
		this.id = id;
		this.username = username;
		this.fullname = fullname;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
		this.status = status;
		this.payments = payments;
		this.orders = orders;
	}

	public Users() {
		// TODO Auto-generated constructor stub
	}

	
	public void setPrice(String fullname2) {
		// TODO Auto-generated method stub
		
	}

	public Object getRole() {
		// TODO Auto-generated method stub
		return null;
	}

}
