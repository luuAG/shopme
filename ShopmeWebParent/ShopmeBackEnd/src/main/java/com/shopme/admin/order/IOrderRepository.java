package com.shopme.admin.order;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Order;

@Repository
public interface IOrderRepository extends PagingAndSortingRepository<Order, Integer> {

	@Query("select o from Order o where concat(o.customer.firstName, o.customer.lastName) like %?1% ")
	public Page<Order> findWithSearch(String keyword, Pageable pageable);
}
