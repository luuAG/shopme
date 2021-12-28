package com.shopme.customer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.AccountUtils;
import com.shopme.AmazonS3Utils;
import com.shopme.FileUtils;
import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;

@Controller
public class AccountController {

	@Autowired
	private CustomerService service;
	
	@GetMapping("/account")
	public String viewAccountDetails(Model model, @AuthenticationPrincipal CustomerDetails loggedDetails, 
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User) {
		Customer  customer;
		if (loggedDetails != null) {
			customer = service.findById(loggedDetails.getId());
		}
		else{
			customer = service.findByEmail(oAuth2User.getEmail());
		}
		
		model.addAttribute("customer", customer);
		model.addAttribute("isLoggedInByDatabase", customer.getAuthenticationType().equals(AuthenticationType.DATABASE));
		
		
		AccountUtils.loadNameAndPhotoToView(model, loggedDetails);
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User);
		return "customers/account_form";
	}
	
	@PostMapping("/account/update")
	public String updateAccount(Model model, @AuthenticationPrincipal CustomerDetails loggedDetails,
			RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomerOAuth2User loggedOAuth2User,
			@RequestParam(value = "fileImage", required = false)MultipartFile photoFile,
			Customer customer) {

		if (photoFile != null && !photoFile.isEmpty()) {
			try {
				saveCustomerInfoAndPhoto(customer, photoFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			customer = service.save(customer);
		}
		
		if (loggedDetails != null) {
			loggedDetails.setFirstName(customer.getFirstName());
			loggedDetails.setPhoto(customer.getPhoto());
		}
		redirectAttributes.addFlashAttribute("message", "Your account details have been saved!");
		return "redirect:/account";
	}
	private void saveCustomerInfoAndPhoto(Customer savedCustomer, MultipartFile fileImage) throws IOException {
		if (!fileImage.isEmpty()) {
			String fileName = fileImage.getOriginalFilename();
			savedCustomer.setPhoto(fileName);
			service.save(savedCustomer);
			
			String uploadDir = "customer-photos/"+savedCustomer.getId();
//			FileUtils.cleanDir(uploadDir);
//			FileUtils.saveFile(uploadDir, fileName, fileImage);
			AmazonS3Utils.deleteFolder(uploadDir);
			AmazonS3Utils.uploadFile(uploadDir, fileName, fileImage.getInputStream());
		}
	}
}
