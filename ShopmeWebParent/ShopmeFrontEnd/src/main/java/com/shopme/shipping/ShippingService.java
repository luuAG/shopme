package com.shopme.shipping;

import java.util.List;

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
	
	public Shipping findByLocation(String location) {
		return repo.findById(location).get();
	}
	
//	public void updateShipping(Shipping shipping) {
//		repo.save(shipping);
//	}
}
