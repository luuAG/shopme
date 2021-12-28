package com.shopme.admin.customer;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AccountUtils;
import com.shopme.admin.AmazonS3Utils;
import com.shopme.common.entity.Customer;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/customers")
	public String viewFirstPage(Model model) {
		return listByPage(1, null, model);
	}
	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum")Integer pageNum,
			@RequestParam("keyword")String keyword,
			Model model) {

		Page<Customer> page = customerService.listByPage(pageNum, keyword);
		List<Customer> listCustomers = page.getContent();
		
		long startCount = (pageNum - 1) * customerService.CUSTOMER_PER_PAGE + 1;
		long endCount = startCount + customerService.CUSTOMER_PER_PAGE - 1;
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
		model.addAttribute("listCustomers", listCustomers);
		model.addAttribute("pageTitle", "Manage customers");
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "customers/customers";
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model) {
		
		Customer customer = customerService.findById(id);
		
		model.addAttribute("customer", customer);
		model.addAttribute("pageTitle", "Edit customer (ID: "+id +")");
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "customers/customer_form";
	}
	
	@GetMapping("/customers/details/{id}")
	public String getCustomerDetails(@PathVariable("id") Integer id, Model model) {
		Customer customer = customerService.findById(id);
		
		model.addAttribute("customer", customer);
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "customers/customer_details_modal";
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, @RequestParam("fileImage")MultipartFile photoFile,
			RedirectAttributes redirectAttributes) {
		
		if (!photoFile.isEmpty() && photoFile != null) {
			try {
				saveCustomerPhoto(customer, photoFile);
			} catch (IOException ex) {
				redirectAttributes.addFlashAttribute("message", "Couldn't save customer! "+ex.getMessage());
				return "redirect:/customers";
			}
			customerService.save(customer);
		}
		else {
			customerService.save(customer);
		}

		redirectAttributes.addFlashAttribute("message", "Customer has been saved successfully");
		return "redirect:/customers";
	}
	private void saveCustomerPhoto(Customer customer, MultipartFile photoFile) throws IOException {
		String fileName = photoFile.getOriginalFilename();
		customer.setPhoto(fileName);
		
//		String uploadDir = StringUtils.cleanPath("../customer-photos/"+customer.getId());
//		FileUtils.cleanDir(uploadDir);
//		FileUtils.saveFile(uploadDir, fileName, photoFile);
		String uploadDir = "customer-photos/"+customer.getId();
		AmazonS3Utils.deleteFolder(uploadDir);
		AmazonS3Utils.uploadFile(uploadDir, fileName, photoFile.getInputStream());
	}

	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable("id")Integer id, RedirectAttributes redirectAttributes) {
		Customer customer = customerService.findById(id);
		if(customer==null) {
			redirectAttributes.addFlashAttribute("message", "Couldn't find customer ID: "+id);
			return "redirect:/customers";
		}
		
		customerService.delete(customer);
		
		redirectAttributes.addFlashAttribute("message", "Customer has been deleted successfully");
		return "redirect:/customers";
	}
}
