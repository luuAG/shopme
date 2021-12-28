package com.shopme.admin.brand;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@RestController
public class BrandRestController {
	@Autowired
	private BrandService service;
	
	@PostMapping("/brands/check_unique")
	public String checkBrandUnique(@Param("id") Integer id, @Param("name") String name) {
		return service.isBrandUnique(id, name) ? "OK" : "Duplicated";
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> getListCorrespondingCategories(@PathVariable(name="id") Integer id) throws BrandNotFoundRestException {
		List<CategoryDTO> listCategories = new ArrayList<>();
		try {
			Brand brand = service.findById(id);
			for(Category category : brand.getCategories()) {
				listCategories.add(new CategoryDTO(category.getId(), category.getName()));
			}
		} catch (NoSuchElementException ex) {
			throw new BrandNotFoundRestException();
		}
		return listCategories;
	}
}
