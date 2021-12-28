package com.shopme.product;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Product;

@Repository
public interface IProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("select p from Product p where (p.category.id=?1 or p.category.allParentIDs like %?2%)"
			+ " and p.enabled=true order by p.name asc")
	public Page<Product> findAllByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);
	
	@Query("select p from Product p where p.alias=?1 and p.enabled=true")
	public Product findByAlias(String alias);
	
	@Query(nativeQuery = true, value = "select * from Products where enabled=true and "
			+ "match(name, description) against (?1)")
	public Page<Product> search(String keyword, Pageable pageable);

}
