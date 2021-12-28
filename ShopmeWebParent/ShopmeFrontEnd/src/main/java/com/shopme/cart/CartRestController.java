package com.shopme.cart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.AccountUtils;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@RestController
public class CartRestController {
	@Autowired
	private CartService cartService;
	@Autowired
	private CustomerService customerService;

	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {

		Customer customer = getAuthenticatedCustomer(request);

		if (customer != null) {
			Integer updatedQuantity = cartService.addProduct(customer.getId(), productId, quantity);
			return "You have added " + updatedQuantity + " to cart";
		} else {
			return "You must login to add product to cart!";
		}

	}

	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		
		Customer customer = getAuthenticatedCustomer(request);

		if (customer != null) {
			float totalPrice = cartService.updateQuantity(customer.getId(), productId, quantity);
			return String.valueOf(totalPrice);
		} else {
			return "You must login to add product to cart!";
		}
	}
	
	@DeleteMapping("/cart/delete/{productId}")
	public String removeProduct(@PathVariable("productId")Integer productId, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);

		if (customer != null) {
			return cartService.removeProduct(customer.getId(), productId) ? 
					"You have removed the product out of cart" :
						"Couldn't remove the product. An error occurs!";
		} else {
			return "You must login to add product to cart!";
		}
	}
	
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = AccountUtils.getEmailAuthenticatedCustomer(request);
		if (email != null) {
			return customerService.findByEmail(email);
		}
		return null;
	}
}
