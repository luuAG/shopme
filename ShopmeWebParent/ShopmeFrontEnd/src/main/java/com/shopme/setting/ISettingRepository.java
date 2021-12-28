package com.shopme.setting;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Setting;

@Repository
public interface ISettingRepository extends CrudRepository<Setting, String> {

}
