package com.shopme.security.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomerOAuth2User implements OAuth2User {

	private OAuth2User oauth2User;
	private String clientName;
	
	
	public CustomerOAuth2User(OAuth2User user, String clientName) {
		this.oauth2User = user;
		this.clientName = clientName;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return oauth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return oauth2User.getAuthorities();
	}

	@Override
	public String getName() {
		return oauth2User.getName();
	}
	
	public String getEmail() {
		return oauth2User.getAttribute("email");
	}
	
	public String getFullName() {
		return oauth2User.getAttribute("name");
	}

	public String getPhotoPath() {
		if (clientName.equals("Facebook"))
			return "https://graph.facebook.com/"+getName()+"/picture?type=large";
		return oauth2User.getAttribute("picture");
	}

	public String getClientName() {
		return clientName;
	}


	

}
