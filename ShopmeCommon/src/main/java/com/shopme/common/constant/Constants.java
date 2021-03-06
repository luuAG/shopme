package com.shopme.common.constant;

public class Constants {
	public static final String S3_BASE_URI;
	
	static {
		String bucketName = System.getenv("AWS_BUCKET");
		String region = System.getenv("AWS_REGION");
		String pattern = "https://%s.s3.%s.amazonaws.com";
		
		System.out.println(bucketName);
		
		String uri = String.format(pattern, bucketName, region);
		
		S3_BASE_URI = bucketName == null ? "" : uri;
	}
	
}
