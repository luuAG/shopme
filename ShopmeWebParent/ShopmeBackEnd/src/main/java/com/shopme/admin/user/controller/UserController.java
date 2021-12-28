package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;


@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping(value = {"/users", "/users/page/", "/users/page"})
	public String listFirstPage(Model model) {

		return listByPage(1, model, null);
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model)
	{
		
		List<Role> listRoles = service.listAllRoles();
		User user = new User();
		user.setEnabled(true);
		
		model.addAttribute("user",user);
		model.addAttribute("listRoles",listRoles);
		model.addAttribute("pageTitle", "Create new user");
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhoto(filename);
			User savedUser = service.save(user);
			String uploadDir = "user-photos/"+savedUser.getId();
			
			//FileUtils.cleanDir(uploadDir);
			//FileUtils.saveFile(uploadDir, filename, multipartFile);
			AmazonS3Utils.deleteFolder(uploadDir);
			AmazonS3Utils.uploadFile(uploadDir, filename, multipartFile.getInputStream());
		}
		else {
			if (user.getPhoto().isEmpty())
				user.setPhoto(null);
			service.save(user);
		}
		
		if (user.getId() == loggedUser.getId()) {
			loggedUser.setFirstName(user.getFirstName());
			loggedUser.setPhoto(user.getPhoto());
		}
		
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
		return "redirect:/users";
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name="id") Integer id, 
			Model model,
			RedirectAttributes redirectAttributes) {
		
		try {
			List<Role> listRoles = service.listAllRoles();
			User user = service.get(id);
			
			model.addAttribute("user", user);
			model.addAttribute("listRoles",listRoles);
			model.addAttribute("pageTitle", "Edit user (ID: "+id+")");
			
			
			AccountUtils.loadNameAndPhotoToView(model);
			return "users/user_form";
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/users";
		}
		
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes)
	{
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user has been deleted!");
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/users";
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, 
			Model model, 
			@Param("keyword")String keyword) {
		Page<User> page = service.listByPage(pageNum,keyword);
		List<User> listUsers = page.getContent();
		
		long totalElements = page.getTotalElements();
		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		if (endCount > totalElements) {
			endCount = totalElements;
		}
		long totalPage = (totalElements % UserService.USERS_PER_PAGE) == 0 ? (totalElements / UserService.USERS_PER_PAGE) : (totalElements / UserService.USERS_PER_PAGE) + 1; 
		if (totalElements <= UserService.USERS_PER_PAGE)
			totalPage = 1;
		
		model.addAttribute("listUser", listUsers);
		model.addAttribute("startCount", startCount);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", totalPage);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalElements", totalElements);
		model.addAttribute("keyword", keyword);

		
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "users/users";
		
	}
	
	
	
}