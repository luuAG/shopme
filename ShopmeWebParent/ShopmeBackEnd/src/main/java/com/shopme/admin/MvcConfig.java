package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		//for user
//		exposeDirectory(registry, "user-photos");
//		// for category
//		exposeDirectory(registry, "../category-images");
//		// for brand
//		exposeDirectory(registry, "../brand-logo");
//		// for products
//		exposeDirectory(registry, "../product-images");
//		// for settings
//		exposeDirectory(registry, "../site-logo");
//		// for customers
//		exposeDirectory(registry, "../customer-photos");
//	}
	
	private void exposeDirectory(ResourceHandlerRegistry registry, String directoryName) {
		Path path = Paths.get(directoryName);
		String absolutePath = path.toFile().getAbsolutePath();
		String logicalPath = directoryName.replace("../", "") + "/**";
		registry.addResourceHandler(logicalPath)
				.addResourceLocations("file:/"+absolutePath+"/");
	}

}
