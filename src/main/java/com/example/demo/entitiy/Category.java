package com.example.demo.entitiy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "category")
	
public class Category {
   

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String name;
	    private String description;

	    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
		@JsonIgnore // sonsuz döngü problemini çözer
	    private List<Product> products;// Bu listeyi JSON'a dahil etme

		//Entity'yi direkt döndürmek yerine, DTO'ya dönüştürünce döngü zaten kırılır çünkü 
	    //DTO'da sadece istediğin alanlar var.
	
}
