package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitiy.Category;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repos.CategoryRepostory;

import java.util.List;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepostory categoryRepository;

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public Category createCategory(Category category) {
	    if (categoryRepository.existsByName(category.getName())) {
	        throw new InvalidRequestException("Bu kategori zaten mevcut: " + category.getName());
	   
	    }
	    return categoryRepository.save(category);
	}

	public Category getCategoryById(Long id) {
	    return categoryRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı: " + id));
		//Category add = new Category();
		//add.setName(category.getName());
		//return categoryRepository.save(category);
	}

	public Category updateCategory(Long id, Category updatedData) {
		Category existing = getCategoryById(id);
		existing.setName(updatedData.getName());
		existing.setDescription(updatedData.getDescription());
		return categoryRepository.save(existing);
	}

	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}
}
