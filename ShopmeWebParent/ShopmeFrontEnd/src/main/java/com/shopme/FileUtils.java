package com.shopme;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
	
	public static void saveFile(String uploadDir, String fileName,
			MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(uploadDir);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		try (InputStream inputStream = multipartFile.getInputStream()){
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		try {
			Files.list(dirPath).forEach(file -> {
				if (!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					}catch (IOException ex) {
						System.out.print(ex.getMessage());
					}
				}
			});
		}catch (IOException ex) {
			System.out.print(ex.getMessage());
		}
	}
	
	public static void deleteWholeDirectory(String logicalDir) {
		Path path = Paths.get(logicalDir);
		try (Stream<Path> walk = Files.walk(path)) {
			walk
				.sorted(Comparator.reverseOrder())
				.forEach(p -> deleteSubDirectory(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteSubDirectory(Path path) {
		try {
			Files.delete(path);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
