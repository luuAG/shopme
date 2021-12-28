package com.shopme.admin.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Customer;

@Repository
public interface ICustomerRepository extends PagingAndSortingRepository<Customer, Integer> { // đã có CRUD cơ bản
	@Query("select c from Customer c where concat(c.firstName, ' ', c.lastName, ' ', c.email) like %?1%")
	public Page<Customer> search(String keyword, Pageable pageable);
	
//	@Query("select c from Customer c")
//	public Page<Customer> findAll(Pageable pageable);
}
