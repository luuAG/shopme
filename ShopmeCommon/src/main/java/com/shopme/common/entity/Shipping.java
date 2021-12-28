package com.shopme.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shipping")
public class Shipping {
	@Id
	private String location;
	@Column(nullable = false, name ="shipping_cost")
	private float shippingCost;
	@Column(nullable = false, name = "expected_delivery_time")
	private Integer expectedDeliveryTime;
	
	
	
	public Shipping() {
	}
	public Shipping(String location, float shippingCost, Integer expectedDeliveryTime) {
		this.location = location;
		this.shippingCost = shippingCost;
		this.expectedDeliveryTime = expectedDeliveryTime;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public float getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}
	public Integer getExpectedDeliveryTime() {
		return expectedDeliveryTime;
	}
	public void setExpectedDeliveryTime(Integer expectedDeliveryTime) {
		this.expectedDeliveryTime = expectedDeliveryTime;
	}
	
	
}
