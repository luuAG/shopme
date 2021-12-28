package com.shopme;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;

import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;

public class AccountUtils {
	public static void loadNameAndPhotoToView(Model model, CustomerDetails customerDetails) {
		if (customerDetails != null) {
			String photoPath = customerDetails.getPhotoPath();
			String fullName = customerDetails.getFullName();

			model.addAttribute("photoPath", photoPath);
			model.addAttribute("fullName", fullName);
		}
	}
	
	public static void loadOAuth2NameAndPhotoToView(Model model, CustomerOAuth2User oAuth2User) {
		if (oAuth2User != null) {
			String photoPath = oAuth2User.getPhotoPath();
			String fullName = oAuth2User.getFullName();

			
			model.addAttribute("photoPath", photoPath);
			model.addAttribute("fullName", fullName);
		}
	}
	
	public static String getEmailAuthenticatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		if (principal == null) return null;
		
		String email = null;
		
		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			email = request.getUserPrincipal().getName();
		}else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			email = oAuth2User.getEmail();
		}
		
		return email;
	}
}
