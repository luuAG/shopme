package com.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopme.AccountUtils;
import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;

@Controller
public class ProductController {
	@Autowired
	private CategoryService categoryService;
	@Autowired 
	private ProductService productService;

	
	@GetMapping("/c/{alias}")
	public String viewFirstPage(@PathVariable("alias")String alias, Model model, 
			@AuthenticationPrincipal CustomerDetails loggedCustomer,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User) {
		return viewByPage(alias, 1, model, loggedCustomer, oAuth2User);
	}
	
	@GetMapping("/c/{alias}/page/{pageNum}")
	public String viewByPage(@PathVariable("alias")String alias, 
			@PathVariable("pageNum")Integer pageNum, Model model,
			@AuthenticationPrincipal CustomerDetails loggedCustomer,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User) {
		
		Category category = categoryService.findByAlias(alias);
		if (category==null) {
			return "error/404";
		}
		List<Category> parentCategories = categoryService.getParents(category);
		
		Page<Product> page = productService.listByPage(pageNum, category.getId());
		List<Product> listProducts = page.getContent();
		
		long startCount = (pageNum - 1) * productService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + productService.PRODUCT_PER_PAGE - 1;
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
		model.addAttribute("title", category.getName());
		model.addAttribute("category", category);
		model.addAttribute("parentCategories", parentCategories);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("keyword", null);
		
		AccountUtils.loadNameAndPhotoToView(model, loggedCustomer);
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User);
		return "products/products_by_category";
	}
	
	@GetMapping("/search")
	public String viewFirstSearchPage(@RequestParam("keyword") String keyword,Model model, 
			@AuthenticationPrincipal CustomerDetails loggedCustomer,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User) {
		return viewSearchResult(1, keyword, model, loggedCustomer, oAuth2User);
	}
	
	@GetMapping(value = {"/search/page/{pageNum}"})
	public String viewSearchResult(@PathVariable("pageNum")Integer pageNum,
			@RequestParam("keyword") String keyword, Model model, 
			@AuthenticationPrincipal CustomerDetails loggedCustomer,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User) {
		Page<Product> page = productService.search(pageNum, keyword);
		List<Product> listProducts = page.getContent();
		long startCount = (pageNum - 1) * productService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + productService.PRODUCT_PER_PAGE - 1;
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
		model.addAttribute("title", keyword);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("keyword", keyword);
		
		AccountUtils.loadNameAndPhotoToView(model, loggedCustomer);
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User);
		return "products/search_result";
	}
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetails(@PathVariable("product_alias")String alias, Model model,
			@AuthenticationPrincipal CustomerDetails loggeCustomer,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User) {
		Product product = productService.getProduct(alias);
		if (product==null) {
			return "error/404";
		}
		Category category = product.getCategory();
		if (category==null) {
			return "error/404";
		}
		List<Category> parentCategories = categoryService.getParents(category);
		
		model.addAttribute("category", category);
		model.addAttribute("parentCategories", parentCategories);
		model.addAttribute("product", product);
		model.addAttribute("title", product.getName());
		
		AccountUtils.loadNameAndPhotoToView(model, loggeCustomer);
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User);
		return "products/product_details";
		
	}
	
	
	
}
