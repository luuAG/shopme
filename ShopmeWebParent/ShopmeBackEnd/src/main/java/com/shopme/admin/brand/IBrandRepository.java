package com.shopme.admin.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.Brand;

public interface IBrandRepository extends PagingAndSortingRepository<Brand, Integer> {

	@Query("select b from Brand b where b.name like %?1%")
	public Page<Brand> searchBrand(String name, Pageable pageable);

	@Query("select b from Brand b where b.name = :name")
	public Brand findByName(@Param("name") String name);

	@Query("select new Brand(b.id, b.name) from Brand b order by b.name ASC")
	public List<Brand> findAll();
	
}
