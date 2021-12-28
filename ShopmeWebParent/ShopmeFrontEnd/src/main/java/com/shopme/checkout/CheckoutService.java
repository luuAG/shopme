package com.shopme.checkout;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Shipping;



@Service
public class CheckoutService {
	
	public CheckoutInfo prepareCheckoutInfo(List<CartItem> cartItems, Shipping shipping,
			String address, String fullName) {
		CheckoutInfo checkoutInfo = new CheckoutInfo();
		
		float productCost = 0f;
		float subTotalPrice = 0f;
		for (CartItem item : cartItems) {
			productCost += item.getProduct().getPrice() * item.getQuantity(); // phuc vu cho thong ke doanh thu
			subTotalPrice += item.getTotalPrice();
		}
		
		checkoutInfo.setFullName(fullName);
		checkoutInfo.setAddress(address);
		checkoutInfo.setProductCost(productCost);
		checkoutInfo.setSubTotalPrice(subTotalPrice);
		checkoutInfo.setShippingCost(shipping.getShippingCost());
		checkoutInfo.setTotalPrice(subTotalPrice + shipping.getShippingCost());
		checkoutInfo.setDeliverTime(getNextDate(shipping.getExpectedDeliveryTime()));
		
		return checkoutInfo;
	}

	private Date getNextDate(int days) {
		return Date.from(Instant.now().plusSeconds(days * 24 * 60 * 60));
	}
}
