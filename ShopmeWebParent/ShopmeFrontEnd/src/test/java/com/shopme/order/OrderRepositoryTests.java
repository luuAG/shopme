package com.shopme.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTests {
	@Autowired IOrderRepository orderRepository;
	@Autowired IOrderDetailRepository orderDetailRepository;
	
	@Test
	public void testDeleteByCustomer() {
		orderDetailRepository.deleteByCustomer(5);
		int result = orderRepository.deleteByCustomer(5);
		assertThat(result).isGreaterThan(0);
	}
}
