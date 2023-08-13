package com.example.springboot.accounting.api;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.model.FileType;
import com.example.springboot.accounting.model.dto.InvoiceCreationRequest;
import com.example.springboot.accounting.model.dto.InvoiceDto;
import com.example.springboot.accounting.model.dto.InvoiceUpdateRequest;
import com.example.springboot.accounting.model.entities.Attachment;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.repository.InvoiceRepository;
import com.example.springboot.accounting.service.InvoiceService;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceApiController {

	private InvoiceRepository invoiceRepo;
	private InvoiceService invoiceService;

	@Autowired
	public InvoiceApiController(InvoiceRepository invoiceRepo,InvoiceService invoiceService) {
		this.invoiceRepo = invoiceRepo;
		this.invoiceService = invoiceService;
	}

	@PostMapping("/create")
	ResponseEntity<Invoice> create(InvoiceCreationRequest request) {
		Invoice in = new Invoice();
		fill(request, in);
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
		in = invoiceRepo.save(in);
		return ResponseEntity.ok(in);
	}

	private void fill(InvoiceDto request, Invoice in) {
		in.setAmount(request.getAmount().doubleValue());
		in.setTps(request.getTps());
		in.setTvq(request.getTvq());
		in.setNoFacture(request.getNoFacture());
		java.sql.Date sqlDate = java.sql.Date.valueOf(request.getDate());
		Date utilDate = new Date(sqlDate.getTime());
		in.setDate(utilDate);
		in.setDescription(request.getDescription());
	}

	@PostMapping("/updateBill")
	public ResponseEntity<Invoice> updateBill(@RequestBody InvoiceUpdateRequest request) {
		Optional<Invoice> invoice = invoiceRepo.findById(request.getId());
		if (invoice.isPresent()) {
			fill(request,invoice.get());
			invoiceRepo.save(invoice.get());
		}

		// Update the bill in the database using the provided data
		// Return a success response or error message as needed
		System.out.println();
		return ResponseEntity.ok(invoice.get());
	}
	
	
	@GetMapping("/findByExpenseId")
	public ResponseEntity<List<Invoice>> getInvoicesByExpenseId(@RequestParam Long expenseId) {
		// Find the invoices that match the given expense ID
		List<Invoice> matchingInvoices = invoiceService.findInvoicesByExpenseId(expenseId);

		// Return the invoices in the response
		return ResponseEntity.ok(matchingInvoices);
	}

}
