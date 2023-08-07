package com.example.springboot.accounting.api;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.model.FileType;
import com.example.springboot.accounting.model.dto.InvoiceCreationRequest;
import com.example.springboot.accounting.model.entities.Attachment;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.repository.InvoiceRepository;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceApiController {

	private InvoiceRepository invoiceRepo;

	@Autowired
	public InvoiceApiController(InvoiceRepository invoiceRepo) {
		this.invoiceRepo = invoiceRepo;
	}

	@PostMapping("/create")
	void create(InvoiceCreationRequest request) {
		Invoice in = new Invoice();
		in.setAmount(request.getAmount().doubleValue());
		Attachment att = new Attachment();
		try {
			att.setFile(request.getFile().getBytes());
			String type = request.getFile().getContentType();
			if (type.equals("application/pdf"))
				att.setType(FileType.PDF);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		in.addAttachment(att);
		invoiceRepo.save(in);
		System.out.println();
	}

	@PostMapping("/updateBill")
	public ResponseEntity<Invoice> updateBill(@RequestBody InvoiceUpdateRequest request) {
		Optional<Invoice> invoice = invoiceRepo.findById(request.getId());
		if (invoice.isPresent()) {
			invoice.get().setAmount(request.getAmount());
			invoice.get().setDescription(request.getDescription());
			java.sql.Date sqlDate = java.sql.Date.valueOf(request.getDate());

			// Convert java.sql.Date to java.util.Date
			Date utilDate = new Date(sqlDate.getTime());
			invoice.get().setDate(utilDate);
			invoiceRepo.save(invoice.get());
		}

		// Update the bill in the database using the provided data
		// Return a success response or error message as needed
		System.out.println();
		return ResponseEntity.ok(invoice.get());
	}

}
