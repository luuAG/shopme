package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Category;


@Service
public class CategoryService {
	
	public static final int ROOT_CATEGORY_PER_PAGE = 1;
	public static final int SEARCH_CATEGORY_PER_PAGE = 4;
	
	@Autowired
	private ICategoryRepository categoryRepo;// DAO

	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String keyword) {

		Sort sortByName = Sort.by("name").ascending();
		
		
		Page<Category> pageCategories = null;
		if (keyword == null || keyword.isEmpty()) {
			Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORY_PER_PAGE, sortByName);
			pageCategories = categoryRepo.findRootCategories(pageable);	
		}
		else {
			Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_CATEGORY_PER_PAGE, sortByName);
			pageCategories = categoryRepo.searchPageCategory(keyword, pageable);
		}	
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		if (keyword == null || keyword.isEmpty()) {
			return listHierarchicalCategories(pageCategories.getContent());
		}
		else {
			return pageCategories.getContent();
		}	
		
	}

	private List<Category> listHierarchicalCategories(List<Category> categories) {
		List<Category> listHierarchicalCategories = new ArrayList<>();

		for (Category category : categories) {

			listHierarchicalCategories.add(Category.copy(category));

			Set<Category> subCategories = sortSubCategories(category.getChildren());

			for (Category subCategory : subCategories) { // duyệt từng thằng con
				String name = "☞" + subCategory.getName();
				subCategory.setName(name);
				listHierarchicalCategories.add(Category.copy(subCategory));
				listChildren(listHierarchicalCategories, subCategory, 1); // in con của con

			}
		}

		return listHierarchicalCategories;
	}

	public List<Category> listCategoriesForForm() {
		Iterable<Category> categoriesInDB = categoryRepo.findRootCategories(Sort.by("name").ascending());
		List<Category> categoriesForForm = new ArrayList<>();

		for (Category category : categoriesInDB) {
			if (category.getParent() == null) {

				categoriesForForm.add(Category.copy(category));

				Set<Category> subCategories = sortSubCategories(category.getChildren());

				for (Category subCategory : subCategories) { // duyệt từng thằng con
					String name = "☞" + subCategory.getName();
					subCategory.setName(name);
					categoriesForForm.add(Category.copy(subCategory));
					listChildren(categoriesForForm, subCategory, 1); // in con của con
				}
			}
		}
		return categoriesForForm;
	}

	private void listChildren(List<Category> categoriesOutput, Category parent, int level) {
		level++; // tăng độ sâu
		Set<Category> children = sortSubCategories(parent.getChildren()); // lấy đàn con
		
		for (Category child : children) { // duyệt từng thằng con
			String name = "";
			for (int i = 0; i < level; i++) { // in độ sâu
				name += "☞";
			}
			name += child.getName();
			child.setName(name);
			categoriesOutput.add(Category.copy(child));
			listChildren(categoriesOutput, child, level); // in con của con
		}
	}

	

	public Category save(Category category) {
		Category parent = category.getParent();
		if (parent != null) {
			String allParentIDs = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIDs += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIDs);
		}
		return categoryRepo.save(category);
	}

	public Category findById(Integer id) throws NoSuchElementException {
		return (Category) categoryRepo.findById(id).get();
	}

	public boolean isCategoryUnique(Integer id, String name, String alias) {
		Category categoryByName = categoryRepo.findByName(name);
		Category categoryByAlias = categoryRepo.findByAlias(alias);
		
		boolean isCreatingNew = (id == null || id == 0);
		
		if (isCreatingNew) {
			if(categoryByName != null || categoryByAlias != null) 
				return false;
			
		}
		else {
			if (categoryByName != null && categoryByName.getId() != id)
				return false;
			if (categoryByAlias != null && categoryByAlias.getId() != id)
				return false;
		}
		
		return true;
		
	}
	
	public Category cleanCategoryName(Category category) {
		while(category.getName().contains("☞")) {
			category.setName(category.getName().replace("☞", ""));
		}
		return category;
	}

	public void deleteById(Integer id)  {
		try {
			Category category = categoryRepo.findById(id).get();
			categoryRepo.delete(category);
			
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException("Couldn't find this item (ID: "+id+ ")");
		} catch (Exception e) {
			throw new IllegalArgumentException("Couldn't find this item (ID: "+id+ ")");
		}
		
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			@Override
			public int compare(Category cat1, Category cat2) {
				return cat1.getName().compareTo(cat2.getName());
			}
		});
		sortedChildren.addAll(children);
		return sortedChildren;
	}
}
