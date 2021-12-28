package com.shopme.cart;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.customer.ICustomerRepository;
import com.shopme.product.IProductRepository;

@Service
@Transactional
public class CartService {
	@Autowired private ICartRepository cartRepo;
	@Autowired private ICustomerRepository customerRepo;
	@Autowired private IProductRepository productRepo;
	
	public List<CartItem> getByCustomer(Integer customerId){
		return cartRepo.findByCustomer(customerRepo.findById(customerId).get());
	}
	
	public void deleteByCustomer(Integer customerId) {
		cartRepo.deleteByCustomer(customerId);
	}
	
	public Integer addProduct(Integer customerId, Integer productId, Integer quantity) {
		Customer customer = customerRepo.findById(customerId).get();
		Product product = productRepo.findById(productId).get();
		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);
		if(cartItem != null) {
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			product.setAmount(product.getAmount() - quantity);
		}else {
			cartItem = new CartItem(product, customer, quantity);
			product.setAmount(product.getAmount() - quantity);
		}
		
		cartRepo.save(cartItem);
		return cartItem.getQuantity();
	}
	
	public float updateQuantity(Integer customerId, Integer productId, Integer quantity) {
		Product product = productRepo.findById(productId).get();
		Integer oldQuantity = cartRepo.findByCustomerAndProduct(customerRepo.findById(customerId).get(), product).getQuantity();
		
		cartRepo.updateQuantity(customerId, productId, quantity);
		
		
		
		Integer oldAmount = product.getAmount();
		product.setAmount(oldAmount + oldQuantity - quantity);
		productRepo.save(product);
		
		return product.getPrice() * quantity;
	}
	
	public boolean removeProduct(Integer customerId, Integer productId) {
		try {			
			Product product = productRepo.findById(productId).get();
			CartItem cart = cartRepo.findByCustomerAndProduct(customerRepo.findById(customerId).get(), product);
			
			Integer oldAmount = product.getAmount();
			Integer quantity = cart.getQuantity();
			
			product.setAmount(oldAmount + quantity);
			productRepo.save(product);
			
			cartRepo.deleteByCustomerAndProduct(customerId, productId);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
}
