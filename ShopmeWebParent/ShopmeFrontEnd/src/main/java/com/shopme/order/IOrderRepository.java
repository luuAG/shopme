package com.shopme.order;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Order;

@Repository
public interface IOrderRepository extends CrudRepository<Order, Integer> {

	@Query("select o from Order o where o.customer.id=?1 order by o.orderedTime")
	public List<Order> findByCustomer(Integer customerId);
	
	@Query("delete from Order o where o.customer.id=?1")
	@Modifying
	public Integer deleteByCustomer(Integer customerId);
}
