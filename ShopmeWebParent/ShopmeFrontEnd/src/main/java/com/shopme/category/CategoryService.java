package com.shopme.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
public class CategoryService {
	@Autowired
	private ICategoryRepository repo;
	
	public List<Category> getNoChildCategories(){
		List<Category> categories = new ArrayList<>();
		for (Category category : repo.findEnabledCategories()) {
			if (!category.hasChildren())
				categories.add(category);
		}
		return categories;
	}
	
	public Category findByAlias(String alias) {
		return repo.findEnabledByAlias(alias);
	}
	
	public List<Category> getParents(Category category) {
		if (category.getAllParentIDs() != null) {
			String[] listParentIDs = category.getAllParentIDs().split("-");
		
			List<Category> parentCategories = new ArrayList<>();
			for (String parentId : listParentIDs) {
				if (!parentId.isEmpty()) {
					Integer id = Integer.parseInt(parentId);
					Category parentCategory = repo.findById(id).get();
					parentCategories.add(parentCategory);
				}
			}
			return parentCategories;
		}
		return null;
	}
}
