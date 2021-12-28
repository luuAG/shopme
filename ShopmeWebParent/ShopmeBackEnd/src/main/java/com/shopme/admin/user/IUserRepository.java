package com.shopme.admin.user;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.User;

@Repository
public interface IUserRepository extends PagingAndSortingRepository<User, Integer> {
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email); 
	
	@Query("SELECT u FROM User u WHERE  CONCAT(u.id, ' ', u.firstName, ' ', u.lastName, ' ', "
			+ " u.email) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
}
