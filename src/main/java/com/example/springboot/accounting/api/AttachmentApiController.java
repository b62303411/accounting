package com.example.springboot.accounting.api;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.service.InvoiceService;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentApiController {

	private final InvoiceService service;

	@Autowired
	public AttachmentApiController(InvoiceService service) {
		this.service = service;
	}

	@GetMapping("/preview/{id}")
	public ResponseEntity<String> previewBill(@PathVariable Long id) throws IOException {
		byte[] imageBytes = service.createPreviewImageAsBytesForInvoice(id);
		String base64Image = Base64.getEncoder().encodeToString(imageBytes);
		return ResponseEntity.ok(base64Image);
	}

}
