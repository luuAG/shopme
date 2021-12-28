package com.shopme.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Repository
public interface ISettingRepository extends CrudRepository<Setting, String> {
	@Query("select s from Setting s where s.category = ?1")
	public List<Setting> findByCategory(SettingCategory category);
}
