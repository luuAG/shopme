package com.shopme.admin.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTests {

	@Autowired private IOrderRepository orderRepo;
	@Autowired private EntityManager entityManager;
	
	@Test
	public void testCreateOrder() {
		Customer customer = entityManager.find(Customer.class, 2);
		Product product = entityManager.find(Product.class, 2);
		
		Order mainOrder = new Order();
		mainOrder.setCustomer(customer);
		mainOrder.setOrderedTime(new Date());
		mainOrder.setDeliverTime(new Date());
		mainOrder.setProductCost(product.getPrice());
		mainOrder.setShippingCost(2);
		mainOrder.setStatus(OrderStatus.NEW);
		mainOrder.setAddress("thu duc");
		
		
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrder(mainOrder);
		orderDetail.setProduct(product);
		orderDetail.setProductCost(product.getPrice());
		orderDetail.setQuantity(2);
		orderDetail.setShippingCost(2);
		orderDetail.setSubTotalPrice(product.getPrice() * orderDetail.getQuantity());
		
		mainOrder.getOrderDetails().add(orderDetail);
		mainOrder.setTotalPrice(orderDetail.getSubTotalPrice());
		
		Order savedOrder = orderRepo.save(mainOrder);
		
		assertThat(savedOrder.getId()).isGreaterThan(0);
	}
	@Test
	public void testCreateMultipleProductOrder() {
		Customer customer = entityManager.find(Customer.class, 5);
		Product product = entityManager.find(Product.class, 2);
		Product product1 = entityManager.find(Product.class, 3);
		
		Order mainOrder = new Order();
		mainOrder.setCustomer(customer);
		mainOrder.setOrderedTime(new Date());
		mainOrder.setDeliverTime(new Date());
		mainOrder.setStatus(OrderStatus.NEW);
		mainOrder.setAddress("somewhere");
		
		
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrder(mainOrder);
		orderDetail.setProduct(product);
		orderDetail.setProductCost(product.getPrice());
		orderDetail.setQuantity(2);
		orderDetail.setShippingCost(2);
		orderDetail.setSubTotalPrice(product.getPrice() * orderDetail.getQuantity());
		
		OrderDetail orderDetail1 = new OrderDetail();
		orderDetail1.setOrder(mainOrder);
		orderDetail1.setProduct(product1);
		orderDetail1.setProductCost(product1.getPrice());
		orderDetail1.setQuantity(2);
		orderDetail1.setShippingCost(2);
		orderDetail1.setSubTotalPrice(product1.getPrice() * orderDetail1.getQuantity());
		
		mainOrder.getOrderDetails().add(orderDetail);
		mainOrder.getOrderDetails().add(orderDetail1);
		
		mainOrder.setProductCost(product.getPrice() + product1.getPrice());
		mainOrder.setSubTotalPrice(product.getPrice()*2 + product1.getPrice()*2);
		mainOrder.setShippingCost(2);
		mainOrder.setTotalPrice(orderDetail.getSubTotalPrice() + orderDetail1.getSubTotalPrice() + mainOrder.getShippingCost());
		
		Order savedOrder = orderRepo.save(mainOrder);
		
		assertThat(savedOrder.getOrderDetails().size()).isEqualTo(2);
	}

	@Test
	public void testDeleteOrder() {
		orderRepo.delete(entityManager.find(Order.class, 2));
	}
}
