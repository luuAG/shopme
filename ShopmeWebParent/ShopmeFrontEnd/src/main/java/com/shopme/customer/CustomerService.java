package com.shopme.customer;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {
	@Autowired
	private ICustomerRepository repository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<Customer> findAll() {
		return (List<Customer>)repository.findAll();
	}
	public Customer findById(Integer id) {
		return repository.findById(id).get();
	}
	public Customer findByEmail(String email) {
		return repository.findByEmail(email);
	}
	public Customer findByVerificationCode(String code) {
		return repository.findByVerificationCode(code);
	}
	public Customer findByResetPasswordToken(String token) {
		return repository.findByResetPasswordToken(token);
	}
	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}
	public Customer registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		
		String verifyCode = RandomString.make(64);
		customer.setVerificationCode(verifyCode);
		return repository.save(customer);
		
	}
	public Customer save(Customer customer) {
		Customer existingCustomer = repository.findById(customer.getId()).get();
		
		existingCustomer.setAddress(customer.getAddress());
		existingCustomer.setFirstName(customer.getFirstName());
		existingCustomer.setLastName(customer.getLastName());
		existingCustomer.setPhoneNumber(customer.getPhoneNumber());
		System.out.println("Pass: "+customer.getPassword());
//		if(customer.getPassword() != null && !customer.getPassword().equals("")) {
//			encodePassword(customer);
//			existingCustomer.setPassword(customer.getPassword());
//		}
		return repository.save(existingCustomer);
	}
	
	public boolean verify(String verifyCode) {
		Customer customer = repository.findByVerificationCode(verifyCode);
		if (customer==null || customer.isEnabled()) {
			return false;
		}else {
			repository.enable(customer.getId());
			return true;
		}
	}
	
	public void updateAuthenticationType(Integer id, AuthenticationType authType) {
		if (! findById(id).getAuthenticationType().equals(authType)) {
			repository.updateAuthenticationType(id, authType);
		}
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer customer = repository.findByEmail(email);
		if(customer == null) return true;
		
		boolean isCreatingNew = (id==null);
		if (isCreatingNew) {
			if (customer != null) return false;
		} else {
			if (customer != null && customer.getId() != id) {
				return false;
			}
		}
		
		return true;
	}
	public void addNewCustomerUponOAuthLogin(String fullName, String email) {
		Customer customer = new Customer();
		customer.setEmail(email);
		customer.setAuthenticationType(AuthenticationType.GOOGLE);
		customer.setPassword("");
		customer.setPhoneNumber("");
		customer.setPhoto("");
		customer.setAddress("");
		customer.setCreatedTime(new Date());
		customer.setEnabled(true);
		setName(customer, fullName);
		
		repository.save(customer);
	}
	private void setName(Customer customer, String fullName) {
		String[] splitedName = fullName.split(" ");
		if (splitedName.length < 2) {
			customer.setFirstName(fullName);
			customer.setLastName("");
		}else {
			String lastName = splitedName[splitedName.length-1];
			customer.setLastName(lastName);
			customer.setFirstName(fullName.replace(" "+lastName, ""));
		}
		
	}
	public String updateResetPasswordToken(String email) throws NoSuchElementException {
		Customer customer = repository.findByEmail(email);
		if(customer != null) {
			String token = RandomString.make(30);
			customer.setResetPasswordToken(token);
			repository.save(customer);
			return token;
		}else {
			throw new NoSuchElementException("Couldn't find user with email: "+email);
		}
		
	}
	public void updatePassword(Customer customer, String password) {
		Customer existingCustomer = repository.findById(customer.getId()).get();
		existingCustomer.setPassword(password);
		encodePassword(existingCustomer);
		existingCustomer.setResetPasswordToken("");
		
		repository.save(existingCustomer);
	}
	
	
}
