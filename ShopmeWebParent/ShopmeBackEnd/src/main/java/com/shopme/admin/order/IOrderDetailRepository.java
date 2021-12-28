package com.shopme.admin.order;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.OrderDetail;

@Repository
public interface IOrderDetailRepository extends CrudRepository<OrderDetail, Integer>{
	@Query(nativeQuery = true, value = "delete from order_details od1 "
			+ "where id in "
			+ "( "
			+ "	select * "
			+ "    from  "
			+ "    ( "
			+ "		select od2.id  "
			+ "		from order_details od2 "
			+ "        join orders o on od2.order_id = o.id "
			+ "		join customers c on o.customer_id = c.id "
			+ "		where c.id = ?1 "
			+ "	) as temp "
			+ ");")
	@Modifying
	public Integer deleteByCustomer(Integer customerId);
}
