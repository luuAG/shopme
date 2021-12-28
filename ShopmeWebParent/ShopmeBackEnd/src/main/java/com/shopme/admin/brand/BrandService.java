package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;

@Service
public class BrandService {
	
	public final int BRANDS_PER_PAGE = 4;

	@Autowired
	private IBrandRepository repo;
	
	
	
	public List<Brand> findAll() {
		return repo.findAll();
	}
	public Page<Brand> listByPage(String keyword, Pageable pageable){
		if (keyword != null) {
			return repo.searchBrand(keyword, pageable);
		}
		return repo.findAll(pageable);
	}
	


	public Brand save(Brand brand) {
		return repo.save(brand);
	}
	
	public Brand findById(Integer id){
		try {
			return repo.findById(id).get();
		}catch (NoSuchElementException ex) {
			throw new NoSuchElementException("Couldn't find this brand ID: "+id);
		}
		
	}
	
	public void delete(Integer id) {
		try {
			repo.delete(findById(id));
		} catch (NoSuchElementException ex) {
			throw new NoSuchElementException("Couldn't find this brand ID: "+id);
		}
	}
	
	public boolean isBrandUnique(Integer id, String name) {
		boolean isCreatingNew = id == null;
		Brand oldBrand = repo.findByName(name);
		if(oldBrand == null)
			return true;
		if (isCreatingNew) {
			if (oldBrand != null)
				return false;
		}else {
			if (oldBrand.getId() != id)
				return false;
		}
		return true;
	}
	
	

}
