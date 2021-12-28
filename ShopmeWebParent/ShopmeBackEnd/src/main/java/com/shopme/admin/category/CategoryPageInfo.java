package com.shopme.admin.category;

public class CategoryPageInfo {
	int totalPages;
	long totalElements;
	
	public CategoryPageInfo(){}
	
	public CategoryPageInfo(int totalPages, int totalElements) {
		this.totalPages = totalPages;
		this.totalElements = totalElements;
	}
	
	
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}
	
	
}
