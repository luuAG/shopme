package com.shopme.common.entity;

import java.util.ArrayList;
import java.util.List;

public class SettingBag {
	private List<Setting> listSettings = new ArrayList<>();

	public SettingBag(List<Setting> listSettings) {
		this.listSettings = listSettings;
	}
	
	public Setting get(String key) {
		for (Setting setting : listSettings) {
			if (setting.getKey().equals(key)) {
				return setting;
			}
		}
		return null;
	}
	
	public String getValue(String key) {
		Setting setting = get(key);
		if (setting != null)
			return setting.getValue();
		return null;
	}
	public void update(String key, String value) {
		Setting setting = get(key);
		if (setting != null && value != null)
			setting.setValue(value);
	}
	
	
	
}
