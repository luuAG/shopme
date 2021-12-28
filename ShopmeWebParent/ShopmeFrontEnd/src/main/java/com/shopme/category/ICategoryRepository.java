package com.shopme.category;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Category;

@Repository
public interface ICategoryRepository extends CrudRepository<Category, Integer> {
	@Query("select c from Category c where c.enabled=true order by c.name ASC")
	public List<Category> findEnabledCategories();
	
	@Query("select c from Category c where c.enabled=true and c.alias=?1")
	public Category findEnabledByAlias(String alias);
}
