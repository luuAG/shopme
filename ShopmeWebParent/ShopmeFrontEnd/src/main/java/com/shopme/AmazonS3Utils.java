package com.shopme;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class AmazonS3Utils {
	public static final String AWS_BUCKET_NAME;
	
	static {
		AWS_BUCKET_NAME = System.getenv("AWS_BUCKET");
	}
	
	public static List<String> listFolder(String folderName) {
		S3Client client = S3Client.builder().build();
		ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
				.bucket(AWS_BUCKET_NAME).prefix(folderName).build();
		ListObjectsResponse response = client.listObjects(listObjectsRequest);
		
		List<S3Object> contents = response.contents();
		
		ListIterator<S3Object> iterator = contents.listIterator();
		
		List<String> listKeys = new ArrayList<>();
		
		while (iterator.hasNext()) {
			S3Object s3Object = (S3Object) iterator.next();
			listKeys.add(s3Object.key());
		}
		
		return listKeys;
	}
	
	public static void uploadFile(String folderName, String fileName, InputStream inputStream) {
		S3Client s3Client = S3Client.builder().build();
		
		PutObjectRequest request = PutObjectRequest.builder()
				.bucket(AWS_BUCKET_NAME)
				.key(folderName + "/" + fileName)
				.acl("public-read")
				.build();
		try (inputStream) {
			int contentLength = inputStream.available();
			s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public static void deleteFile(String fileName) {
		S3Client s3Client = S3Client.builder().build();
		
		DeleteObjectRequest request = DeleteObjectRequest.builder()
				.bucket(AWS_BUCKET_NAME)
				.key(fileName)
				.build();
		s3Client.deleteObject(request);

	}
	
	public static void deleteFolder(String folderName) {
		S3Client client = S3Client.builder().build();
		
		ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
				.bucket(AWS_BUCKET_NAME).prefix(folderName).build();
		ListObjectsResponse response = client.listObjects(listObjectsRequest);
		List<S3Object> contents = response.contents();
		ListIterator<S3Object> iterator = contents.listIterator();
		
		while (iterator.hasNext()) {
			S3Object s3Object = (S3Object) iterator.next();
			DeleteObjectRequest request = DeleteObjectRequest.builder()
					.bucket(AWS_BUCKET_NAME)
					.key(s3Object.key())
					.build();
			client.deleteObject(request);
		}

	}
}
