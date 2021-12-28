package com.shopme.order;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.AccountUtils;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;

@Controller
public class OrderController {

	@Autowired OrderService orderService;
	@Autowired CustomerService customerService;
	
	@GetMapping(value = {"/orders"})
	public String viewOrders(Model model, HttpServletRequest request,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User,
			@AuthenticationPrincipal CustomerDetails customerDetails) {
		String email = AccountUtils.getEmailAuthenticatedCustomer(request);
		Customer customer = customerService.findByEmail(email);
		String option = request.getParameter("filter");
		
		if (option == null || option.equals("delivered")) {
			model.addAttribute("orders", orderService.findByCustomer(customer.getId(), true));
		}else {
			model.addAttribute("orders", orderService.findByCustomer(customer.getId(), false));
		}
		
		model.addAttribute("filter", option);
		AccountUtils.loadNameAndPhotoToView(model, customerDetails);
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User);
		return "orders/orders";
	}
	
}
