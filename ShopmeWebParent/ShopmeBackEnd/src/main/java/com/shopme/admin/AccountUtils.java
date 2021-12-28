package com.shopme.admin;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.shopme.admin.security.ShopmeUserDetails;

public class AccountUtils {
	public static void loadNameAndPhotoToView(Model model) {
		ShopmeUserDetails userDetails = (ShopmeUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		String photoPath = userDetails.getPhotoPath();
		String firstName = userDetails.getFirstName();

		model.addAttribute("photoPath", photoPath);
		model.addAttribute("firstName", firstName);
	}
}
