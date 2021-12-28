package com.shopme.admin.category;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

	@Autowired
	private ICategoryRepository repo;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = repo.save(category);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(8);
		Category subCategory = new Category("iPhone", parent);

		repo.save(subCategory);

	}
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(2).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildren();
		children.forEach(child -> System.out.println(child.getName()));
		
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> categories = repo.findAll();
		
		for (Category category : categories) {
			if (category.getParent() == null) {
				System.out.println(category.getName());
				
				Set<Category> subCategories = category.getChildren();

				subCategories.forEach(subCategory ->{
					System.out.println("--" + subCategory.getName());
					printChildren(subCategory, 1);
				});
			}
		}
	}
	@Test
	public void testListRootCategories() {
		List<Category> rootCategories =repo.findRootCategories(Sort.by("name"));
		rootCategories.forEach(cate -> System.out.println(cate.getName()));
	}
	
	@Test
	public void testFindByNameAndAlias() {
		System.out.println(repo.findByName("Computers").getId());
		System.out.println(repo.findByAlias("Computers").getId());
	}
	
	private void printChildren(Category parent, int level) {
		level ++; // tăng độ sâu	
		Set<Category> children = parent.getChildren(); // lấy đàn con
		
		for(Category child : children) { // duyệt từng thằng con
			for(int i=0; i<level; i++) { // in độ sâu 
				System.out.print("--");		
			}
			System.out.println(child.getName()); // in tên 1 đứa con
			printChildren(child, level); // in con của con
		}
	}
}
