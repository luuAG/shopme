package com.shopme.admin.product;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.AmazonS3Utils;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

public class ProductSaveHelper {
	public static  String setMainImageName(Product product, MultipartFile mainImageFile) throws IOException {
		if (!mainImageFile.isEmpty()) {
			String filename = StringUtils.cleanPath(mainImageFile.getOriginalFilename());
			product.setMainImage(filename);
			return filename;
		}
		return null;
	}

	public static  void setExistingExtraImagesNames(String[] imageIDs, String[] imageNames, Product product) {
		if (imageIDs == null || imageIDs.length == 0)
			return;

		Set<ProductImage> extraImages = new HashSet<>();

		for (int i = 0; i < imageIDs.length; i++) {
			extraImages.add(new ProductImage(Integer.parseInt(imageIDs[i]), imageNames[i], product));
		}
		product.setImages(extraImages);

	}

	public static  String[] setNewExtraImagesNames(Product product, MultipartFile[] extraImageFiles) {
		if (extraImageFiles != null && extraImageFiles.length > 0) {
			String[] fileNames = new String[extraImageFiles.length];
			for (int i = 0; i < extraImageFiles.length; i++) {
				if (!extraImageFiles[i].isEmpty()) {
					fileNames[i] = StringUtils.cleanPath(extraImageFiles[i].getOriginalFilename());
					if (!product.containsImageName(fileNames[i])) {
						product.addExtraImage(fileNames[i]);
					}
				}
			}
			return fileNames;
		}
		return null;
	}

	public static  void saveUploadedImages(Product savedProduct, String mainImageName, String[] extraImageNames,
			MultipartFile mainImageFile, MultipartFile[] extraImageFiles) throws IOException {

		String mainImageUploadDir = "product-images/" + savedProduct.getId() + "/main";
		String extraImageUploadDir = "product-images/" + savedProduct.getId() + "/extra";

		if (mainImageName != null && !mainImageName.isEmpty()) {
//			FileUtils.cleanDir(mainImageUploadDir);
//			FileUtils.saveFile(mainImageUploadDir, mainImageName, mainImageFile);
			AmazonS3Utils.deleteFolder(mainImageUploadDir);
			AmazonS3Utils.uploadFile(mainImageUploadDir, mainImageName, mainImageFile.getInputStream());
		}
		if (extraImageNames.length > 0 && extraImageNames != null) {
			for (int i = 0; i < extraImageNames.length; i++) {
				if (!extraImageFiles[i].isEmpty()) {
//					FileUtils.saveFile(extraImageUploadDir, extraImageNames[i], extraImageFiles[i]);
					AmazonS3Utils.uploadFile(extraImageUploadDir, extraImageNames[i], extraImageFiles[i].getInputStream());
				}

					
			}
		}
	}

	public static  void deleteRemovedExtraImages(Product savedProduct) {
		//Path extraDirPath = Paths.get("../product-images/" + savedProduct.getId() + "/extra");
		
//		try {
//			Files.list(extraDirPath).forEach(file -> {
//				String fileName = file.toFile().getName();
//				if (!savedProduct.containsImageName(fileName)) {
//					try {
//						Files.delete(file);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//			
//		} catch (IOException ex) {
//			System.out.println(ex.getMessage());
//		}
		String extraImageDir = "/product-images/" + savedProduct.getId() + "/extra";
		AmazonS3Utils.deleteFolder(extraImageDir);
	}

	public static  void setDetails(Product product, String[] detailNames, String[] detailValues) {
		if (detailNames.length == 0) {
			product.getDetails().clear();
			return;
		}
		if (detailNames != null && detailValues != null && detailValues.length > 0) {
			product.getDetails().clear();
			for (int i = 0; i < detailNames.length; i++) {
				product.addDetail(detailNames[i], detailValues[i]);
			}
		}
	}
}
