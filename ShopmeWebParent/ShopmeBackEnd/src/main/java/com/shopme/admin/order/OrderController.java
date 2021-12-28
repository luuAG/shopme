package com.shopme.admin.order;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AccountUtils;
import com.shopme.admin.product.ProductService;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.Product;



@Controller
public class OrderController {

	@Autowired private OrderService orderService;
	@Autowired private OrderDetailService orderDetailService;
	@Autowired private ProductService productService;

	
	@GetMapping("/orders")
	public String viewFirstPage(Model model) {
		return viewByPage(1, null, model);
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String viewByPage(@PathVariable("pageNum") Integer pageNum,
			@RequestParam(name = "keyword", required = false) String keyword, Model model) {
		Page<Order> page = orderService.listByPage(pageNum, keyword);
		List<Order> listOrders = page.getContent();
		long startCount = (pageNum - 1) * orderService.ORDER_PER_PAGE + 1;
		long endCount = startCount + orderService.ORDER_PER_PAGE - 1;
		long totalElements = page.getTotalElements();
		int totalPages = page.getTotalPages();
		if (endCount > totalElements) {
			endCount = totalElements;
		}
		
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalElements", totalElements);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listOrders", listOrders);
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "orders/orders";
	}
	
	@GetMapping("/orders/details/{orderId}")
	public String viewDetails(@PathVariable("orderId")Integer orderId, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Order order = orderService.findById(orderId);
			model.addAttribute("order", order);
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/orders";
		}
		
		return "orders/order_details_modal";
	}
	
	@GetMapping("/orders/delete/{orderId}")
	public String deleteOrder(@PathVariable("orderId")Integer orderId, RedirectAttributes redirectAttributes) {
		try {
			orderService.deleteById(orderId);
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/orders";
		}
		redirectAttributes.addFlashAttribute("message", "Order has been deleted successfully!");
		return "redirect:/orders";
	}
	
	@GetMapping("/orders/edit/{orderId}")
	public String editOrder(@PathVariable("orderId") Integer orderId, 
			Model model, RedirectAttributes redirectAttributes) {
		Order order = null;
		try {
			order = orderService.findById(orderId);
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage()); 
			return "redirect:/orders";
		}

		model.addAttribute("order", order);
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "orders/order_form";
	}
	
	@PostMapping("/orders/save")
	public String saveOrder(Order order, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		
		updateOrderDetails(request, order);

		try {
			orderService.update(order);
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		redirectAttributes.addFlashAttribute("message", "Order has been updated successfully!");
		return "redirect:/orders";
	}
	private void updateOrderDetails(HttpServletRequest request, Order order) {
		String[] detailId = request.getParameterValues("detailId");
		String[] quantity = request.getParameterValues("quantity");
		String[] subTotalPrice = request.getParameterValues("subTotalPricePerProduct");
		int count = detailId.length;
		
		Set<OrderDetail> orderDetails = new HashSet<>();
		
		for (int i = 0; i < count; i++) {	
			Integer id = Integer.parseInt(detailId[i]);
			Integer newQuantity = Integer.parseInt(quantity[i]);
			Float subTotal = Float.parseFloat(subTotalPrice[i]);
			OrderDetail detail = orderDetailService.findById(id);
			
			//update quantity for product in database
			Product product = detail.getProduct();
			Integer oldQuantity = detail.getQuantity();
			product.setAmount(product.getAmount() + oldQuantity - newQuantity);
			productService.update(product);
			
			detail.setQuantity(newQuantity);
			detail.setSubTotalPrice(subTotal);
			
			orderDetails.add(detail);
		}
		
		order.setOrderDetails(orderDetails);	
	}
}
