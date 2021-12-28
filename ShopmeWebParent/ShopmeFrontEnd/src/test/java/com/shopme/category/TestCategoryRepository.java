package com.shopme.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TestCategoryRepository {
	@Autowired
	private ICategoryRepository categoryRepository;

	@Test
	public void testFindEnabledCategories() {
		List<Category> categories = categoryRepository.findEnabledCategories();

		categories.forEach(cate -> System.out.println(cate.getName() + " " + cate.isEnabled()));
	}
	
	@Test
	public void testFindByAlias() {
		assertThat(categoryRepository.findEnabledByAlias("camera_flashes").getId()).isGreaterThan(0);
	}
}
