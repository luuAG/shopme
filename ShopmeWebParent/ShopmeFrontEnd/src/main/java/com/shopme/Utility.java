package com.shopme;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.shopme.setting.EmailSettingBag;

public class Utility {
	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		siteURL.replace(request.getServletPath().toString(), "");
		return siteURL;
	}
	
	public static JavaMailSenderImpl prepareJavaMail(EmailSettingBag emailSettingBag) {
		JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
		mailSenderImpl.setHost(emailSettingBag.getHost());
		mailSenderImpl.setPort(emailSettingBag.getPort());
		mailSenderImpl.setUsername(emailSettingBag.getUsername());
		mailSenderImpl.setPassword(emailSettingBag.getPassword());
		
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", emailSettingBag.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable", emailSettingBag.getSmtpSecured());
		
		mailSenderImpl.setJavaMailProperties(mailProperties);
		return mailSenderImpl;
	}
}
