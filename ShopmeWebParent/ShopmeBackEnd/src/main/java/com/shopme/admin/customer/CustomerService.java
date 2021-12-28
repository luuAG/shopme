package com.shopme.admin.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;

@Service
public class CustomerService {
	public final Integer CUSTOMER_PER_PAGE = 5;
	
	@Autowired // Dependency Injection
	private ICustomerRepository repository;
	
	public Page<Customer> listByPage(Integer pageNum, String keyword){
		Sort sortById = Sort.by("id").ascending();
		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMER_PER_PAGE, sortById);
		if (keyword==null)
			return repository.findAll(pageable);
		return repository.search(keyword, pageable);
	}

	public Customer findById(Integer id) {
		return repository.findById(id).get();
	}

	public void save(Customer customer) {
		Customer existingCustomer = repository.findById(customer.getId()).get();
		existingCustomer.setEnabled(customer.isEnabled());
		existingCustomer.setPhoto(customer.getPhoto());
		if (!customer.getPassword().isEmpty() && customer.getPassword()!=null) {
			existingCustomer.setPassword(customer.getPassword());
		}
		repository.save(existingCustomer);
	}

	public void delete(Customer customer) {
		repository.delete(customer);
		
	}
}
