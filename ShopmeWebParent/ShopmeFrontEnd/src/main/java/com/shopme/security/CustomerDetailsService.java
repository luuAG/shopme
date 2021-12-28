package com.shopme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shopme.common.entity.Customer;
import com.shopme.customer.ICustomerRepository;

public class CustomerDetailsService implements UserDetailsService{
	@Autowired
	private ICustomerRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customer customer = repo.findByEmail(username);
		if(customer != null) {
			return new CustomerDetails(customer);
		}else {
			throw new UsernameNotFoundException("Couldn't find user with email: "+username);
		}
	}

}
