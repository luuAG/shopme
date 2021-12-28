package com.shopme.admin.order;

import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.product.IProductRepository;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.Product;

@Service
public class OrderService {
	public final int ORDER_PER_PAGE = 10;

	@Autowired private IOrderRepository orderRepo;
	@Autowired private IProductRepository productRepo;
	
	public Page<Order> listByPage(Integer pageNum, String keyword){
		Sort sortByDate = Sort.by("orderedTime");
		Pageable pageable = PageRequest.of(pageNum - 1, ORDER_PER_PAGE, sortByDate);
		if (keyword == null) {
			return orderRepo.findWithSearch("", pageable);
		}
		return orderRepo.findWithSearch(keyword, pageable);
	}
	
	public Order findById(Integer id) throws NoSuchElementException {
		return orderRepo.findById(id).get();
	}
	
	public void deleteById(Integer id) throws NoSuchElementException {
		Order existingOrder = orderRepo.findById(id).get();
		Set<OrderDetail> existingDetails = existingOrder.getOrderDetails();
		
		//rollback the amount
		for (OrderDetail oldDetail : existingDetails) {
			Product product = oldDetail.getProduct();
			Integer oldQuantity = oldDetail.getQuantity();
			boolean flag = false;
			for (OrderDetail newDetail : existingOrder.getOrderDetails()) {
				if (newDetail.getProduct().getId() == product.getId()) {
					flag = true;
					Integer newQuantity = newDetail.getQuantity();
					System.out.println(oldQuantity +" " + newQuantity);
					product.setAmount(product.getAmount() + oldQuantity - newQuantity);
					
					productRepo.save(product);
				}
			}
			if (flag) {
				product.setAmount(product.getAmount() + oldQuantity);
				productRepo.save(product);
			}
		}
		
		orderRepo.delete(existingOrder);
	}
	
	public void update(Order order) {
		Order existingOrder = orderRepo.findById(order.getId()).get();
		
		if (existingOrder.getId() == order.getId()) {
			existingOrder.setSubTotalPrice(order.getSubTotalPrice());
			existingOrder.setShippingCost(order.getShippingCost());
			existingOrder.setProductCost(order.getSubTotalPrice());
			existingOrder.setTotalPrice(order.getTotalPrice());
			existingOrder.setStatus(order.getStatus());
			existingOrder.setDeliverTime(order.getDeliverTime());
			existingOrder.setAddress(order.getAddress());
			existingOrder.setOrderDetails(order.getOrderDetails());
			
			orderRepo.save(existingOrder);
		}else {
			throw new NoSuchElementException("Couldn't update order!");
		}
			
	}
	
}
