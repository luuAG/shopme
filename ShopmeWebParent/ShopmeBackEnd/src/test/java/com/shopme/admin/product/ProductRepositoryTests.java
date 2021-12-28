package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired
	private IProductRepository repo;
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 1);
		Category category = entityManager.find(Category.class, 5);
		
		Product product = new Product();
		product.setName("Acer desktop");
		product.setAlias("acer-desktop");
		product.setDescription("Acer desktop description");
		product.setMainImage("a.jpg");
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		product.setPrice(100000);
		product.setBrand(brand);
		product.setCategory(category);
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testGetProduct() {
		Product product = repo.findById(3).get();
		System.out.println(product.getName());
	}
	@Test
	public void testUpdateProduct() {
		Product product = repo.findById(3).get();
		product.setPrice(190000);
		repo.save(product);
		
		assertThat(entityManager.find(Product.class, 3).getPrice()).isEqualTo(190000);
	}
	@Test
	public void testSaveProductWithExtraImages() {
		Product product = repo.findById(48).get();
		
		product.addExtraImage("1");
		product.addExtraImage("2");
		product.addExtraImage("3");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
}
