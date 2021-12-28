package com.shopme.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;

@Service
public class SettingService {
	@Autowired
	private ISettingRepository repo;
	
	public List<Setting> findAll() {
		return (List<Setting>) repo.findAll();
	}
	
	public EmailSettingBag getEmailSettingBag() {
		return new EmailSettingBag(findAll());
	}
	
}
