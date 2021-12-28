package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
public class ProductService {

	public final int NUMBER_PRODUCT_PER_PAGE = 5;
	
	@Autowired
	private IProductRepository repo;
	
	public List<Product> findAll(){
		return (List<Product>) repo.findAll();
	}
	public Product findById(Integer id) throws NoSuchElementException {
		Product product = null;
		try {
			product = repo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException("Couldn't find product ID: " + id);
		}
		return product;
	}
	public Page<Product> listByPage(Integer pageNum, String keyword, Integer categoryId){
		Sort sortByName = Sort.by("name").ascending();
		Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_PRODUCT_PER_PAGE, sortByName);
		
		if(keyword != null && !keyword.isEmpty() && categoryId != null && categoryId > 0) {
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return repo.searchWithCategory(categoryId, categoryIdMatch, keyword, pageable);
		}
		
		if (keyword != null && !keyword.isEmpty()) {
			return repo.findAll(keyword, pageable);
		}
		
		if (categoryId != null && categoryId > 0) {
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return repo.findAllByCategory(categoryId, categoryIdMatch, pageable);
		}
		return repo.findAll(pageable);
	}
	public Product save(Product product) {
		boolean isCreatingNew = product.getId() == null;
		if (isCreatingNew) {
			product.setCreatedTime(new Date());
			product.setUpdatedTime(new Date());
		}else {
			Product productTemp = repo.findById(product.getId()).get();
			product.setCreatedTime(productTemp.getCreatedTime());
			product.setUpdatedTime(new Date());
		}
		
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			product.setAlias(product.getName().replaceAll(" ", "-"));
			product.setAlias(product.getName().replaceAll("/", ""));
		}else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
			product.setAlias(product.getAlias().replaceAll("/", ""));
		}
		
		
		return repo.save(product);
	}
	
	public boolean isUnique(Integer id, String name) {
		boolean isCreatingNew = id == null;
		
		if (isCreatingNew) {
			if (repo.findByName(name) != null) {
				return false;
			}
		}
		else {
			if (repo.findByName(name) != null && repo.findByName(name).getId() != id)
				return false;
		}
		return true;
	}
	
	public void delete(Integer id) throws IllegalArgumentException {
		try {
			repo.deleteById(id);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Couldn't find product with ID: "+id);
		}
		
	}
	
	public void update(Product product) {
		repo.save(product);
	}
}
