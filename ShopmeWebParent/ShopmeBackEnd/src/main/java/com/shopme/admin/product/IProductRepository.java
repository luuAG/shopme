package com.shopme.admin.product;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Product;

@Repository
public interface IProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("select p from Product p where p.name=:name")
	public Product findByName(@Param("name")String name);
	
	@Query("select p from Product p where concat(p.name, ' ', p.description, ' ', p.brand) like %?1%")
	public Page<Product> findAll(String keyword, Pageable pageable);
	
	@Query("select p from Product p where p.category.id=?1 or p.category.allParentIDs like %?2%")
	public Page<Product> findAllByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);
	
	@Query("select p from Product p where (p.category.id=?1 or p.category.allParentIDs like %?2%) "
			+ "and concat(p.name, ' ', p.description, ' ', p.brand) like %?3%") // not SQL, ma la JPQL ----> ORM
	public Page<Product> searchWithCategory(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);
}
