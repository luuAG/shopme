package com.shopme.checkout;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.AccountUtils;
import com.shopme.Utility;
import com.shopme.cart.CartService;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.Shipping;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;
import com.shopme.shipping.ShippingService;

@Controller
public class CheckoutController {
	@Autowired CartService cartService;
	@Autowired CheckoutService checkoutService;
	@Autowired CustomerService customerService;
	@Autowired SettingService settingService;
	@Autowired OrderService orderService;
	@Autowired ShippingService shippingService;
	
	
	@GetMapping("/checkout")
	public String viewCheckout(String fullName, String address, String location, Model model, HttpServletRequest request,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User,
			@AuthenticationPrincipal CustomerDetails customerDetails) {
		
		String email = AccountUtils.getEmailAuthenticatedCustomer(request);
		Customer customer = customerService.findByEmail(email);
		List<CartItem> cartItems = cartService.getByCustomer(customer.getId());
		Shipping shipping = shippingService.findByLocation(location);
		
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckoutInfo(
				cartItems, shipping, address, fullName);
		
		Order savedOrder = orderService.save(customer, cartItems, checkoutInfo);
		
		try {
			sendOrderConfirmationEmail(customer, checkoutInfo, savedOrder);
			cartService.deleteByCustomer(savedOrder.getCustomer().getId());
			
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("checkoutInfo", checkoutInfo);
		
		AccountUtils.loadNameAndPhotoToView(model, customerDetails); // login bang database
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User); // login bang google voi facebook
		return "checkout/checkout";
	}
	private void sendOrderConfirmationEmail(Customer customer, CheckoutInfo checkoutInfo,
			Order savedOrder) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettingBag = settingService.getEmailSettingBag();
		JavaMailSenderImpl mailSender = Utility.prepareJavaMail(emailSettingBag);
		
		String toAddress = customer.getEmail();
		String subject = emailSettingBag.getOrderConfirmSubject();
		String content = emailSettingBag.getOrderConfirmContent();
		String products = "<br>";
		for (OrderDetail orderDetail : savedOrder.getOrderDetails()) {
			products += "   - "+ orderDetail.getQuantity() + " x " + orderDetail.getProduct().getName() +"<br>";
		}

		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(message);
		
		messageHelper.setTo(toAddress);
		messageHelper.setFrom(emailSettingBag.getSenderAddress(), emailSettingBag.getUsername());
		messageHelper.setSubject(subject);

		content = content.replace("[[customer]]", customer.getFullName());
		content = content.replace("[[orderId]]", savedOrder.getId().toString());
		content = content.replace("[[recipient]]", checkoutInfo.getFullName());
		content = content.replace("[[address]]", checkoutInfo.getAddress());	
		content = content.replace("[[products]]", products);
		content = content.replace("[[totalPrice]]", String.valueOf(checkoutInfo.getTotalPrice()));
		
		
		messageHelper.setText(content, true);
		
		mailSender.send(message);
		
		System.out.println("to address: " + toAddress);
		
	}
}
