package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
import com.shopme.admin.FileUtils;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String listAll(Model model) {

		return listByPage(1, model, null, 0);
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum")Integer pageNum, Model model,
			@RequestParam("keyword")String keyword,
			@RequestParam("categoryId")Integer categoryId) {
		if (keyword!=null && !keyword.isEmpty()) {
			keyword = keyword.trim();
		}
		
		List<Category> listCategories = categoryService.listCategoriesForForm();
		
		Page<Product> page = productService.listByPage(pageNum, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		long startCount = (pageNum - 1) * productService.NUMBER_PRODUCT_PER_PAGE + 1;
		long endCount = startCount + productService.NUMBER_PRODUCT_PER_PAGE - 1;
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
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("categoryId", categoryId);

		AccountUtils.loadNameAndPhotoToView(model);
		return "products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		List<Brand> listBrands = brandService.findAll();

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create new product");

		AccountUtils.loadNameAndPhotoToView(model);
		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam("fileMainImage") MultipartFile mainImageFile,
			@RequestParam("fileExtraImage") MultipartFile[] extraImageFiles,
			@RequestParam(name = "detailName", required = false) String[] detailNames,
			@RequestParam(name = "detailValue", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {
		String mainImage = ProductSaveHelper.setMainImageName(product, mainImageFile);
		ProductSaveHelper.setExistingExtraImagesNames(imageIDs, imageNames, product);
		String[] extraImages = ProductSaveHelper.setNewExtraImagesNames(product, extraImageFiles);
		ProductSaveHelper.setDetails(product, detailNames, detailValues);

		Product savedProduct = productService.save(product);
		ProductSaveHelper.deleteRemovedExtraImages(savedProduct);
		ProductSaveHelper.saveUploadedImages(savedProduct, mainImage, extraImages, mainImageFile, extraImageFiles);
		

		redirectAttributes.addFlashAttribute("message",
				"The product '" + product.getName() + "' has been saved successfully");
		return "redirect:/products";
	}

	

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			productService.delete(id);
			FileUtils.deleteWholeDirectory("product-images/"+id);

			redirectAttributes.addFlashAttribute("message",
					"The product (ID: " + id + ") has been deleted successfully");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/products";
	}

	

	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {
			Product product = productService.findById(id);
			List<Brand> listBrands = brandService.findAll();

			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit product (ID: " + id + ")");
			model.addAttribute("product", product);
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/products";
		}

		AccountUtils.loadNameAndPhotoToView(model);
		return "products/product_form";
	}
	
	@GetMapping("/products/details/{id}")
	public String productDetail(@PathVariable(name = "id") Integer id, Model model) {
		Product product = productService.findById(id);
		
		
		model.addAttribute("product", product);
		
		return "products/product_details_modal";
	}
}
