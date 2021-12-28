package com.shopme.checkout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckoutInfo {
	private String fullName;
	private String address;
	private float shippingCost;
	private float subTotalPrice;
	private float productCost;
	private float totalPrice;
	private Date deliverTime;
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public float getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}
	public float getSubTotalPrice() {
		return subTotalPrice;
	}
	public void setSubTotalPrice(float subTotalPrice) {
		this.subTotalPrice = subTotalPrice;
	}
	public float getProductCost() {
		return productCost;
	}
	public void setProductCost(float productCost) {
		this.productCost = productCost;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Date getDeliverTime() {
		return deliverTime;
	}
	public String getFormatedDeliverTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(deliverTime);
	}
	public void setDeliverTime(Date deliverTime) {
		this.deliverTime = deliverTime;
	}
	
	
}
