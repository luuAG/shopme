package com.shopme.security.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;


@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Autowired
	private CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
		
		System.out.println("Success: "+oAuth2User.getName()+ " | "+ oAuth2User.getEmail()+" | "+oAuth2User.getClientName());
		
		Customer customer = customerService.findByEmail(oAuth2User.getEmail());
		if (customer == null) {
			customerService.addNewCustomerUponOAuthLogin(oAuth2User.getFullName(), oAuth2User.getEmail());
		}else {
			if (oAuth2User.getClientName().equals("Facebook"))
				customerService.updateAuthenticationType(customer.getId(), AuthenticationType.FACEBOOK);
			else
				customerService.updateAuthenticationType(customer.getId(), AuthenticationType.GOOGLE);
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
