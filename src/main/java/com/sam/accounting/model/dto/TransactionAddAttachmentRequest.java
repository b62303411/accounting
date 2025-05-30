package com.sam.accounting.model.dto;

import org.springframework.web.multipart.MultipartFile;
/**
 * 
 */
public class TransactionAddAttachmentRequest {
	private Long id;
	private MultipartFile file;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
	
}
