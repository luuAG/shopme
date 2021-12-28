package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopme.common.entity.Category;


@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;//Dependency Injection

	@GetMapping(value = { "/categories",  "/categories/page/", "/categories/page/" })
	public String listFirstPage(Model model) {
		return listByPage(1, null, model);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum,
			@Param("keyword") String keyword,
			Model model) {
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		
		
		List<Category> listCategories = service.listByPage(pageInfo, pageNum, keyword);
		
		long startCount;
		long endCount;
		if (keyword == null || keyword.isEmpty()) {
			startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORY_PER_PAGE + 1;
			endCount = startCount + CategoryService.ROOT_CATEGORY_PER_PAGE - 1;
		}
		else {
			startCount = (pageNum - 1) * CategoryService.SEARCH_CATEGORY_PER_PAGE + 1;
			endCount = startCount + CategoryService.SEARCH_CATEGORY_PER_PAGE - 1;
		}
		
		
		int totalPages = pageInfo.getTotalPages();
		long totalElements = pageInfo.getTotalElements();
		
		if (endCount > totalElements) {
			endCount = totalElements;
		}
		
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalElements", totalElements);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("keyword", keyword);

		AccountUtils.loadNameAndPhotoToView(model);
		return "categories/categories";
	}

	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> categories = service.listCategoriesForForm();

		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create new category");
		model.addAttribute("categories", categories);

		AccountUtils.loadNameAndPhotoToView(model);
		return "categories/category_form";
	}

	@PostMapping("/categories/save")
	public String saveCategory(Category category, 
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes)
			throws IOException {
		// sửa tên
		service.cleanCategoryName(category);
		
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(filename);
			Category savedCategory = service.save(category);
			
			String uploadDir = "category-images/" + savedCategory.getId();
//			FileUtils.cleanDir(uploadDir);
//			FileUtils.saveFile(uploadDir, filename, multipartFile);
			AmazonS3Utils.deleteFolder(uploadDir);
			AmazonS3Utils.uploadFile(uploadDir, filename, multipartFile.getInputStream());
		}
		else {
			service.save(category);
		}
		
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
		return "redirect:/categories";
	}
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes redirectAttributes) {
		
		try {
		Category category = service.findById(id);
		List<Category> categories = service.listCategoriesForForm();
		
		service.cleanCategoryName(category);
		
		model.addAttribute("category", category);
		model.addAttribute("categories", categories);
		model.addAttribute("pageTitle", "Edit category (ID: " + id + ")");
		}catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", "Could not find category with ID: " + id);
			return "redirect:/categories";
		}
		
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "categories/category_form";
	}
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes) {
		try {
			if (service.findById(id).hasChildren()) {
				redirectAttributes.addFlashAttribute("message", "Cannot delete this item because it has lower-level item!");
				return "redirect:/categories";
			}else {
				service.deleteById(id);
				redirectAttributes.addFlashAttribute("message", "Item has been deleted successfully!");
				return "redirect:/categories";
			}
		}catch (NoSuchElementException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		}
		
	}
	
}
