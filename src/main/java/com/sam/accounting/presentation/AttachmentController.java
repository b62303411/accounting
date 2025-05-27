package com.sam.accounting.presentation;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sam.accounting.service.InvoiceService;

@Controller
@RequestMapping("/view/attachment")
public class AttachmentController {

	private InvoiceService invoiceRepo;

	@Autowired
	public AttachmentController(InvoiceService invoiceRepo) {
		this.invoiceRepo = invoiceRepo;
	}

	@GetMapping("/{id}")
	public String viewPdf(Model model, @PathVariable("id") Long id) {
		try {
			byte[] imageBytes = invoiceRepo.createPreviewImageAsBytesForInvoice(id);
			String base64Image = Base64.getEncoder().encodeToString(imageBytes);
			model.addAttribute("image", base64Image);
		} catch (IOException e) {
	
			e.printStackTrace();
		}

		return "ViewFile";
	}

}
