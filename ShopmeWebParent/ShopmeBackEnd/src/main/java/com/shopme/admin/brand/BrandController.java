package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AccountUtils;
import com.shopme.admin.AmazonS3Utils;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {

	@Autowired
	private BrandService service;

	@Autowired
	private CategoryService categoryService;


	@GetMapping("/brands")
	public String listBrands(Model model) {
		return listByPage(1, model, null);
	}

	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
			@Param("keyword") String keyword) {
		
		Sort sortByName = Sort.by("name").ascending();		
		Pageable pageable = PageRequest.of(pageNum - 1, service.BRANDS_PER_PAGE, sortByName);
		Page<Brand> page = service.listByPage(keyword, pageable);

		List<Brand> listBrands = page.getContent();
		
		long startCount = (pageNum - 1) * service.BRANDS_PER_PAGE + 1;
		long endCount = startCount + service.BRANDS_PER_PAGE - 1;
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
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("keyword", keyword);

		AccountUtils.loadNameAndPhotoToView(model);
		return "brands/brands";
	}

	@GetMapping("/brands/new")
	public String newBrand(Model model) {

		List<Category> categoriesForForm = categoryService.listCategoriesForForm();

		model.addAttribute("brand", new Brand());
		model.addAttribute("listCategories", categoriesForForm);
		model.addAttribute("pageTitle", "Adding new brand");

		AccountUtils.loadNameAndPhotoToView(model);
		return "brands/brand_form";
	}

	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {

		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(filename);
			// sua ten catogories
			for (Category cate : brand.getCategories()) {
				cate = categoryService.cleanCategoryName(cate);
			}
			Brand savedBrand = service.save(brand);

			String uploadDir = "brand-logo/" + savedBrand.getId();
//			FileUtils.cleanDir(uploadDir);
//			FileUtils.saveFile(uploadDir, filename, multipartFile);
			AmazonS3Utils.deleteFolder(uploadDir);
			AmazonS3Utils.uploadFile(uploadDir, filename, multipartFile.getInputStream());
		} else {
			service.save(brand);
		}

		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
		return "redirect:/brands";
	}

	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The brand has been deleted successfully");
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/brands";
		}

		return "redirect:/brands";
	}

	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		Brand brand = null;
		try {
			brand = service.findById(id);
		} catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/brands";
		}
		List<Category> categoriesForForm = categoryService.listCategoriesForForm();

		model.addAttribute("brand", brand);
		model.addAttribute("listCategories", categoriesForForm);
		model.addAttribute("pageTitle", "Editing a brand (ID: " + id + ")");
		AccountUtils.loadNameAndPhotoToView(model);
		return "brands/brand_form";
	}

}
