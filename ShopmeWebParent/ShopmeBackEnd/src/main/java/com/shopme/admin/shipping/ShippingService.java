package com.shopme.admin.shipping;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Shipping;

@Service
public class ShippingService {
	@Autowired
	private IShippingRepository repo;
	
	public List<Shipping> findAll() {
		return (List<Shipping>) repo.findAll();
	}
	
	public Shipping findByLocation(String location) throws NoSuchElementException {
		return repo.findById(location).get();
	}
	
	public void updateShipping(Shipping shipping) {
		repo.save(shipping);
	}

	public void delete(Shipping shipping) {
		repo.delete(shipping);
		
	}
}
