package com.shopme.cart;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;

@Repository
public interface ICartRepository extends CrudRepository<CartItem, Integer> {

	public List<CartItem> findByCustomer(Customer customer);
	
	public CartItem findByCustomerAndProduct(Customer customer, Product product);
	
	@Query("update CartItem ci set ci.quantity=?3 where ci.customer.id=?1 and ci.product.id=?2")
	@Modifying
	public void updateQuantity(Integer customerId, Integer productId, Integer quantity);
	
	@Query("delete from CartItem ci where ci.customer.id=?1 and ci.product.id=?2")
	@Modifying
	public void deleteByCustomerAndProduct(Integer customerId, Integer productId);
	
	@Query("delete from CartItem ci where ci.customer.id=?1")
	@Modifying
	public void deleteByCustomer(Integer customerId);
}
