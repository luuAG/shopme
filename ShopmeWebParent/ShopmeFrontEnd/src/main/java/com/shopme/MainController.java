package com.shopme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Product;
import com.shopme.product.ProductService;
import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth2.CustomerOAuth2User;

@Controller
public class MainController {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;
	
	@GetMapping(value = {"", "/"})
	public String viewHomePage(Model model, @AuthenticationPrincipal CustomerDetails loggeCustomer,
			@AuthenticationPrincipal CustomerOAuth2User oAuth2User) {
		List<Product> allProducts = productService.findAll();
		
		model.addAttribute("listNoChildCategories", categoryService.getNoChildCategories());
		model.addAttribute("allProducts", allProducts);

		AccountUtils.loadNameAndPhotoToView(model, loggeCustomer);
		AccountUtils.loadOAuth2NameAndPhotoToView(model, oAuth2User);
		return "index";
	}
	
	@GetMapping("/login")
	public String login() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication==null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		return "redirect:/";
	}
}
