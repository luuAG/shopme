package com.shopme.security;

import java.util.Collection;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shopme.common.entity.Customer;

public class CustomerDetails implements UserDetails{
	private static final long serialVersionUID = 1L;
	
	private Customer customer;
	
	
	public CustomerDetails(Customer customer) {
		this.customer = customer;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		return customer.getPassword();
	}

	@Override
	public String getUsername() {
		return customer.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return customer.isEnabled();
	}

	public String getPhotoPath() {
		return customer.getPhotoPath();
	}

	public String getFullName() {
		return customer.getFirstName() + " " +customer.getLastName();
	}
	public void setFirstName(String firstName) {
		customer.setFirstName(firstName);
	}
	public void setPhoto(String photo) {
		customer.setPhoto(photo);
	}
	public Integer getId() {
		return customer.getId();
	}

	public Customer getCustomer() {
		return this.customer;
	}

}
