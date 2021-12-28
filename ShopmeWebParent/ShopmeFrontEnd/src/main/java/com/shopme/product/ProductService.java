package com.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
public class ProductService {
	public final Integer PRODUCT_PER_PAGE = 4;
	
	@Autowired
	private IProductRepository repo;
	
	public Page<Product> listByPage(Integer pageNum, Integer categoryId){
		Sort sortByName = Sort.by("name").ascending();
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sortByName);

		String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
		return repo.findAllByCategory(categoryId, categoryIdMatch, pageable);
	}
	
	public Page<Product> search(Integer pageNum, String keyword) {
		Sort sortByName = Sort.by("name").ascending();
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sortByName);

		return repo.search(keyword, pageable);
	}
	
	public Product getProduct(String alias) {
		return repo.findByAlias(alias);
	}
	
	public List<Product> findAll(){
		return (List<Product>) repo.findAll();
	}
}
