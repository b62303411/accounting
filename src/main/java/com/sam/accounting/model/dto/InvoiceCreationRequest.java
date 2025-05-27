package com.sam.accounting.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class InvoiceCreationRequest extends InvoiceDto {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
