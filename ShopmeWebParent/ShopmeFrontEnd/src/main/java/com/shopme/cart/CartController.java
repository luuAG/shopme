package com.shopme.cart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.AccountUtils;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Shipping;
import com.shopme.customer.CustomerService;
import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;
import com.shopme.shipping.ShippingService;

@Controller
public class CartController {
	@Autowired private CartService cartService;
	@Autowired private CustomerService customerService;
	@Autowired private ShippingService shippingService;	
	@GetMapping("/cart")
	public String viewAllCartItem(Model model, HttpServletRequest request,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User,
			@AuthenticationPrincipal CustomerDetails customerDetails) {
		String email = AccountUtils.getEmailAuthenticatedCustomer(request);
		Customer customer = customerService.findByEmail(email);
		List<CartItem> cartItems = cartService.getByCustomer(customer.getId());
		List<Shipping> shippings = shippingService.findAll();
		
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("customer", customer);
		model.addAttribute("shippings", shippings);
		

		AccountUtils.loadNameAndPhotoToView(model, customerDetails);
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User);
		return "cart/cart";
	}
	
}
