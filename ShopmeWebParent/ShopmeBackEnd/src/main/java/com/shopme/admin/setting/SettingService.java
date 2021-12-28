package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.AmazonS3Utils;
import com.shopme.admin.FileUtils;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private ISettingRepository repo;
	
	public List<Setting> findAll() {
		return (List<Setting>) repo.findAll();
	}
	public List<Setting> findByCategory(SettingCategory category){
		return repo.findByCategory(category);
	}
	
	public void saveGeneralSettings(String siteName, String copyright, MultipartFile logoFile) throws IOException {
		Setting setting;
		
		setting = repo.findById("SITE_NAME").get();
		setting.setValue(siteName);
		repo.save(setting);
		
		setting = repo.findById("COPYRIGHT").get();
		setting.setValue(copyright);
		repo.save(setting);
		
		if (!logoFile.isEmpty()) {
			String fileLogoName = StringUtils.cleanPath(logoFile.getOriginalFilename());
			setting = repo.findById("SITE_LOGO").get();
			setting.setValue("/site-logo/"+fileLogoName);
			repo.save(setting);
			
			//FileUtils.cleanDir("../site-logo");
			//FileUtils.saveFile("../site-logo", fileLogoName, logoFile);
			AmazonS3Utils.deleteFile("site-logo/"+fileLogoName);
			AmazonS3Utils.uploadFile("site-logo", fileLogoName, logoFile.getInputStream());
		}
		
	}
	public void saveMailServerSettings(HttpServletRequest request, List<Setting> mailServerSettings) {
		if (mailServerSettings != null) 
			for(Setting setting : mailServerSettings) {
				String key = setting.getKey();
				Setting copySetting = repo.findById(key).get();
				if (copySetting != null) {
					String newValue = request.getParameter(key);
					// adjust newValue, it will be NULL if checkbox is unchecked
					if (key.equals("SMTP_AUTH") || key.equals("SMTP_SECURED")) {
						if (newValue == null || newValue.isBlank() || newValue.isEmpty()) 
							newValue = "false";	
						if (newValue.equals("on"))
							newValue = "true";
					}
										
					// save
					copySetting.setValue(newValue);
					repo.save(copySetting);
				}		
				

			}	
	}
	
	public void saveMailTemplateSettings(HttpServletRequest request, List<Setting> mailTemplateSettings) {
		if (mailTemplateSettings != null) 
			for(Setting setting : mailTemplateSettings) {
				String key = setting.getKey();
				Setting copySetting = repo.findById(key).get();
				if (copySetting != null) {
					String newValue = request.getParameter(key);
					if (newValue != null) {
						copySetting.setValue(newValue);
						repo.save(copySetting);
					}
				}		
				
			}	
	}
}
