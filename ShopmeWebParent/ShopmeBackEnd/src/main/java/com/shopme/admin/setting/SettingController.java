package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AccountUtils;
import com.shopme.admin.shipping.ShippingService;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;
import com.shopme.common.entity.Shipping;

@Controller
public class SettingController {
	@Autowired
	private SettingService settingService;
	@Autowired
	private ShippingService shippingService;
	
	@GetMapping("/settings")
	public String viewSettings(Model model) {
		List<Setting> listSettings = settingService.findAll();
		listSettings.forEach(setting -> {
			model.addAttribute(setting.getKey(), setting.getValue());
		});
		
		List<Shipping> shippings = shippingService.findAll();
		
		model.addAttribute("pageTitle", "Settings");
		model.addAttribute("shippings", shippings);
		
		AccountUtils.loadNameAndPhotoToView(model);
		return "settings/settings";
	}
	
	@PostMapping("/settings/save_shipping/{location}")
	public String saveShipping(@PathVariable("location")String locationID,
			@RequestParam(name = "location", required = false) String location,
			@RequestParam(name = "cost", required = false) Float cost,
			@RequestParam(name = "time", required = false) Integer time) {
		
		locationID = locationID.replaceAll("%20", " ");
		try {
			Shipping shipping = shippingService.findByLocation(locationID);
			shipping.setExpectedDeliveryTime(time);
			shipping.setShippingCost(cost);
					
			shippingService.updateShipping(shipping);	
		} catch (Exception e) {
			Shipping newShipping = new Shipping(locationID, cost, time);
			shippingService.updateShipping(newShipping);
		}

		return "redirect:/settings";
	}
	
	@GetMapping("/settings/delete_shipping/{location}")
	public String deleteShipping(@PathVariable("location") String locationID) {

		locationID = locationID.replaceAll("%20", " ");
		try {
			Shipping shipping = shippingService.findByLocation(locationID);
			shippingService.delete(shipping);
		}catch (Exception e) {
			return "redirect:/settings";
		}
		
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("SITE_LOGO")MultipartFile logoFile,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		String siteName = request.getParameter("SITE_NAME");
		String copyright = request.getParameter("COPYRIGHT");
		try {
			settingService.saveGeneralSettings(siteName, copyright, logoFile);
		} catch (IOException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/settings";
		}
		redirectAttributes.addFlashAttribute("message", "Settings have been saved successfully");
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_mailserver")
	public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		List<Setting> mailServerSettings = settingService.findByCategory(SettingCategory.MAIL_SERVER);
		settingService.saveMailServerSettings(request, mailServerSettings);
		redirectAttributes.addFlashAttribute("message", "Settings have been saved successfully");
		return "redirect:/settings";	
	}
	
	@PostMapping(value = {"/settings/save_verification_template", "/settings/save_confirmation_template"})
	public String saveVerificationSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		List<Setting> mailTemplateSettings = settingService.findByCategory(SettingCategory.MAIL_TEMPLATE);
		settingService.saveMailTemplateSettings(request, mailTemplateSettings);
		redirectAttributes.addFlashAttribute("message", "Settings have been saved successfully");
		return "redirect:/settings";	
	}
}
