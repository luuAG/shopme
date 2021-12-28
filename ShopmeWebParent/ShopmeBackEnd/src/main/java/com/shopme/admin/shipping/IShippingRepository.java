package com.shopme.admin.shipping;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Shipping;

@Repository
public interface IShippingRepository extends CrudRepository<Shipping, String> {

}
