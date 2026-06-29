package com.example.demo.entitiy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "payment")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double amount;

	private Integer instalment;

	private String description;

	private Boolean status;

	private String fullname;

	@Column(name = "bank_name")
	private String bankName;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users user;

	@OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
	private List<Orders> orders = new ArrayList<>();

	public Payment() {
	}

	// --- Getter & Setter'lar ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getInstalment() {
		return instalment;
	}

	public void setInstalment(Integer instalment) {
		this.instalment = instalment;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(boolean b) {
		this.status = b;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Object object) {
		this.user = (Users) object;
	}

	public List<Orders> getOrders() {
		return orders;
	}

	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}

	// Listeye sipariş eklerken iki tarafı da senkronize eden yardımcı metod
	public void addOrder(Orders order) {
		orders.add(order);
		order.setPayment(this);
	}

	public void setMockTransactionId(String string) {
		// TODO Auto-generated method stub

	}

	public void setPaymentDate(LocalDateTime now) {
		// TODO Auto-generated method stub

	}

	public void setPaymentStatus(String string) {
		// TODO Auto-generated method stub

	}
}