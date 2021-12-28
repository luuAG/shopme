package com.shopme.cart;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartRepositoryTests {
	@Autowired private ICartRepository cartRepo;
	@Autowired private EntityManager entityManager;
	
	@Test
	public void testCreateCartItem() {
		Integer customerId = 2;
		Integer productId = 2;
		
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		CartItem cartItem = new CartItem(product, customer, 1);
		CartItem savedItem = cartRepo.save(cartItem);
		
		assertThat(savedItem.getId()).isGreaterThan(0);
		
	}
}
