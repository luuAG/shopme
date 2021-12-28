package com.shopme.admin.user.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Utils;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;

@Controller
public class AccountController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser,
			Model model) {
		String email = loggedUser.getUsername();
		User user = service.getUserByEmail(email);
		
		model.addAttribute("user", user);
		
		loadNameAndPhotoToView(loggedUser,model);
		return "users/account_form";
	}
	private void loadNameAndPhotoToView(ShopmeUserDetails userDetails, Model model) {
		String photoPath = userDetails.getPhotoPath();
		String firstName = userDetails.getFirstName();
		
		model.addAttribute("photoPath", photoPath);
		model.addAttribute("firstName", firstName);
	}
	
	@PostMapping("/account/update")
	public String saveDetails(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhoto(filename);
			User savedUser = service.updateAccount(user);
			String uploadDir = "user-photos/"+savedUser.getId();
			
//			FileUtils.cleanDir(uploadDir);
//			FileUtils.saveFile(uploadDir, filename, multipartFile);
			AmazonS3Utils.deleteFolder(uploadDir);
			AmazonS3Utils.uploadFile(uploadDir, filename, multipartFile.getInputStream());
		}
		else {
			if (user.getPhoto().isEmpty())
				user.setPhoto(null);
			service.updateAccount(user);
		}
		
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setPhoto(user.getPhoto());
		
		redirectAttributes.addFlashAttribute("message", "Your account has been updated successfully");
	
		return "redirect:/account";
	}
}
