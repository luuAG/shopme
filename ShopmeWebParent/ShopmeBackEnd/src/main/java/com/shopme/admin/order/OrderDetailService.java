package com.shopme.admin.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.OrderDetail;

@Service
public class OrderDetailService {
	@Autowired IOrderDetailRepository orderDetailRepository;
	
	public OrderDetail findById(Integer id) {
		return orderDetailRepository.findById(id).get();
	}
}
