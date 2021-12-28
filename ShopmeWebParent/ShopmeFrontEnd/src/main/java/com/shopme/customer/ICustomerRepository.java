package com.shopme.customer;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;

@Repository
public interface ICustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
	@Query("select c from Customer c where c.email like ?1")
	public Customer findByEmail(String email);
	
	@Query("select c from Customer c where c.verificationCode like ?1")
	public Customer findByVerificationCode(String verificationCode);
	
	@Query("update Customer c set c.enabled = true, c.verificationCode = null where c.id = ?1")
	@Modifying
	public void enable(Integer id);
	
	@Query("update Customer c set c.authenticationType = ?2 where c.id=?1")
	@Modifying
	public void updateAuthenticationType(Integer id, AuthenticationType authenticationType);
	
	@Query("select c from Customer c where c.resetPasswordToken = ?1")
	public Customer findByResetPasswordToken(String token);
}
