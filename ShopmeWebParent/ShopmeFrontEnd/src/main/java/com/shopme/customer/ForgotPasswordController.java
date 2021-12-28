package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class ForgotPasswordController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private SettingService settingService;

	@GetMapping("/forgot_password")
	public String viewRequestResetPassword() {
		return "customers/forgot_password/request";
	}

	@PostMapping("/request_reset_password")
	public String requestResetPassword(@RequestParam("email") String email,
			Model model, HttpServletRequest request) {
		try {
			String token = customerService.updateResetPasswordToken(email);
			String resetLink = Utility.getSiteURL(request).replace("/request_reset_password", "") 
					+ "/reset_password?token=" + token;
			sendResetPasswordEmail(resetLink, email);
			
			model.addAttribute("message", "Reset link has been sent to your email");
		} catch (NoSuchElementException ex) {
			model.addAttribute("error", ex.getMessage());
		} 
		catch (UnsupportedEncodingException | MessagingException ex) {
			model.addAttribute("error", "Couldn't send email!");
		}
		return "customers/forgot_password/request";
	}
	private void sendResetPasswordEmail(String link, String email)
			throws MessagingException, UnsupportedEncodingException {
		EmailSettingBag emailSettingBag = settingService.getEmailSettingBag();
		JavaMailSenderImpl mailSender = Utility.prepareJavaMail(emailSettingBag);

		String subject = "Reset your password";
		String content = "<p>You have requested to reset your password</p>" + "<p>Here is your reset link:</p>"
				+ "<a href=" + link + ">Reset link</a>" + "<br>"
				+ "<p>Ignore this mail if you didn't made the request.</p>" + "<p>Shopme CEO - Nam dep trai</p>";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(message);

		messageHelper.setTo(email);
		messageHelper.setFrom(emailSettingBag.getSenderAddress(), emailSettingBag.getUsername());
		messageHelper.setSubject(subject);
		messageHelper.setText(content, true);

		mailSender.send(message);
	}
	
	@GetMapping("/reset_password")
	public String viewResetPasswordForm(@RequestParam("token")String token, Model model, RedirectAttributes redirectAttributes) {
		Customer customer = customerService.findByResetPasswordToken(token);
		if (customer != null) {
			model.addAttribute("email", customer.getEmail());
			return "customers/forgot_password/reset_password";
		}else {
			redirectAttributes.addFlashAttribute("error", "Invalid token");
			return "redirect:/login";
		}
	}
	
	@PostMapping("/reset_password/reset")
	public String resetPassword(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		Customer customer = customerService.findByEmail(email);
		if (customer != null) {
			customerService.updatePassword(customer, password);
			redirectAttributes.addFlashAttribute("message", "Password has been reset");
			return "redirect:/login";
		}
		redirectAttributes.addFlashAttribute("error", "Couldn't reset your password! Try again or contact Shopme");
		return "redirect:/login";
	}
}
