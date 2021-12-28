package com.shopme.setting;

import java.util.List;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

public class EmailSettingBag extends SettingBag {

	public EmailSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}
	
	public String getHost() {
		return super.getValue("MAIL_HOST");
	}
	public Integer getPort() {
		return Integer.parseInt(super.getValue("MAIL_PORT"));
	}
	public String getUsername() {
		return super.getValue("MAIL_USERNAME");
	}
	public String getPassword() {
		return super.getValue("MAIL_PASSWORD");
	}
	public String getSenderAddress() {
		return super.getValue("MAIL_FROM");
	}
	public String getSenderName() {
		return super.getValue("MAIL_SENDER_NAME");
	}
	public String getSmtpAuth() {
		return super.getValue("SMTP_AUTH");
	}
	public String getSmtpSecured() {
		return super.getValue("SMTP_SECURED");
	}
	public String getVerifyContent() {
		return super.getValue("CUSTOMER_VERIFY_CONTENT");
	}
	public String getVerifySubject() {
		return super.getValue("CUSTOMER_VERIFY_SUBJECT");
	}
	public String getOrderConfirmSubject() {
		return super.getValue("ORDER_CONFIRM_SUBJECT");
	}
	public String getOrderConfirmContent() {
		return super.getValue("ORDER_CONFIRM_CONTENT");
	}

}
