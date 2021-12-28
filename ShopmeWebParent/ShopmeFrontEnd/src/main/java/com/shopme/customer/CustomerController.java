package com.shopme.customer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.AccountUtils;
import com.shopme.AmazonS3Utils;
import com.shopme.FileUtils;
import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/register")
	public String register(Model model, @AuthenticationPrincipal CustomerDetails loggedCustomer,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User) {
		
		model.addAttribute("pageTitle", "Registration");
		model.addAttribute("customer", new Customer());
		
		AccountUtils.loadNameAndPhotoToView(model, loggedCustomer);
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User);
		return "customers/register_form";
	}
	
	@PostMapping("/registration_request")
	public String registrationRequest(Customer customer, Model model, HttpServletRequest request,
			@RequestParam("fileImage")MultipartFile fileImage) {
		
		String fileImageName = fileImage.getOriginalFilename();
		customer.setPhoto(fileImageName);
		Customer savedCustomer = customerService.registerCustomer(customer);	
		try {
			saveCustomerPhoto(savedCustomer, fileImage);
			sendVerificationEmail(request, customer);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		model.addAttribute("pageTitle", "Verify your account");
		return "/customers/register_success";
	}
	private void saveCustomerPhoto(Customer savedCustomer, MultipartFile fileImage) throws IOException {
		if (!fileImage.isEmpty()) {
			String fileName = fileImage.getOriginalFilename();
			
			String uploadDir = "customer-photos/"+savedCustomer.getId();
//			FileUtils.cleanDir(uploadDir);
//			FileUtils.saveFile(uploadDir, fileName, fileImage);
			AmazonS3Utils.deleteFolder(uploadDir);
			AmazonS3Utils.uploadFile(uploadDir, fileName, fileImage.getInputStream());
		}
	}

	private void sendVerificationEmail(HttpServletRequest request, Customer customer) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettingBag = settingService.getEmailSettingBag();
		JavaMailSenderImpl mailSender = Utility.prepareJavaMail(emailSettingBag);
		
		String toAddress = customer.getEmail();
		String subject = emailSettingBag.getVerifySubject();
		String content = emailSettingBag.getVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(message);
		
		messageHelper.setTo(toAddress);
		messageHelper.setFrom(emailSettingBag.getSenderAddress(), emailSettingBag.getUsername());
		messageHelper.setSubject(subject);

		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
		content = content.replace("[[URL]]", verifyURL);
		content = content.replace("[[name]]", customer.getFirstName() + " " + customer.getLastName());
		
		messageHelper.setText(content, true);
		
		mailSender.send(message);
		
		System.out.println("to address: " + toAddress);
		System.out.println("URL: "+ verifyURL);
		
		
	}

	@GetMapping("/registration_request/verify")
	public String verify(@RequestParam("code") String code, Model model) {
		boolean isVerified = customerService.verify(code);
		
		model.addAttribute("message", 
				isVerified ? "Your account has been verified successfully!" 
						: "Your account has been verified or verification code is invalid");
		return isVerified ? "customers/verify_success" : "customers/verify_fail";
	}
}
