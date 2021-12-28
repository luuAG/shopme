package com.shopme.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class AmazonS3UtilsTest {
	@Test
	public void testListFolder() {
		String folderName = "user-photos";
		AmazonS3Utils.listFolder(folderName).forEach(key -> System.out.println(key));;
	}
	@Test
	public void testUploadFile() {
		String folder = "test/two";
		String fileName = "circle.png";
		String filePath = "D:\\OneDrive - Unicorn\\Pictures\\Saved Pictures\\Trivial\\" + fileName;
		
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		AmazonS3Utils.uploadFile(folder, fileName, inputStream);
	}
	@Test
	public void testDeleteFile() {
		String fileName = "test/circle.png";
		AmazonS3Utils.deleteFile(fileName);
	}
	@Test
	public void testDeleteFolder() {
		String folder = "test";
		AmazonS3Utils.deleteFolder(folder);
	}
}
