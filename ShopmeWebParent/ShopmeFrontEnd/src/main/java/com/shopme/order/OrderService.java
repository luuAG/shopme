package com.shopme.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.OrderStatus;

@Service
@Transactional
public class OrderService {
	
	@Autowired IOrderRepository orderRepository;
	@Autowired IOrderDetailRepository orderDetailRepository;
	
	public Order save(Customer customer, List<CartItem> cartItems, CheckoutInfo checkoutInfo) {
		
		float subTotalPrice = 0;
		float productCost = 0;
		float totalPrice = 0;
		
		Order order = new Order();
		order.setCustomer(customer);
		order.setOrderedTime(new Date());
		order.setDeliverTime(checkoutInfo.getDeliverTime());
		order.setStatus(OrderStatus.NEW);
		order.setAddress(checkoutInfo.getAddress());
		order.setShippingCost(checkoutInfo.getShippingCost());
		order.setRecipient(checkoutInfo.getFullName());
		
		for (CartItem item : cartItems) {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(order);
			orderDetail.setProduct(item.getProduct());
			orderDetail.setProductCost(item.getProduct().getPrice());
			orderDetail.setQuantity(item.getQuantity());
			orderDetail.setShippingCost(0);
			orderDetail.setSubTotalPrice(item.getProduct().getPrice() * orderDetail.getQuantity());
			
			productCost += orderDetail.getProductCost();
			subTotalPrice += orderDetail.getProductCost() * orderDetail.getQuantity();	
			order.setProductCost(productCost);
			order.setSubTotalPrice(subTotalPrice);
			
			order.getOrderDetails().add(orderDetail);
		}
		
		totalPrice = order.getSubTotalPrice() + checkoutInfo.getShippingCost();
		order.setTotalPrice(totalPrice);
		
		return orderRepository.save(order);
	}

	
	public List<Order> findByCustomer(Integer customerId, boolean isDelivered){
		List<Order> orders = orderRepository.findByCustomer(customerId);
		List<Order> resultOrders = new ArrayList<>();
		
		if (isDelivered) {
			orders.forEach(order -> {
				if (order.getStatus().equals(OrderStatus.DELIVERED)) {
					resultOrders.add(order);
				}
			});
		}
		else {
			orders.forEach(order -> {
				if (!order.getStatus().equals(OrderStatus.DELIVERED)) {
					resultOrders.add(order);
				}
			});
		}
		return resultOrders;
	}
	
}
