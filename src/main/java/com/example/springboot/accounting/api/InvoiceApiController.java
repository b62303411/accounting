package com.example.springboot.accounting.api;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.accounting.model.FileType;
import com.example.springboot.accounting.model.dto.InvoiceCreationRequest;
import com.example.springboot.accounting.model.dto.InvoiceDto;
import com.example.springboot.accounting.model.dto.InvoiceUpdateRequest;
import com.example.springboot.accounting.model.entities.Attachment;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.repository.InvoiceRepository;
import com.example.springboot.accounting.service.InvoiceService;
import com.example.springboot.accounting.service.OpenAiRequestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceApiController {

	private InvoiceRepository invoiceRepo;
	private InvoiceService invoiceService;
	private OpenAiRequestService service;
	@Autowired
	public InvoiceApiController(InvoiceRepository invoiceRepo,InvoiceService invoiceService,OpenAiRequestService service) {
		this.invoiceRepo = invoiceRepo;
		this.invoiceService = invoiceService;
		this.service=service;
	}

	@PostMapping("/parse")
	ResponseEntity<Invoice> parse(@RequestParam("file") MultipartFile file)
	{
		Invoice in = new Invoice();
		try {
			String text = extractTextFromPDF(file);
			String responseString = service.submitInvoiceQuery(text);
			ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(responseString);

            // Check for errors
            if (responseJson.has("error")) {
                String errorMessage = responseJson.get("error").get("message").asText();
                System.out.println("Error from OpenAI: " + errorMessage);
                return null;
            }

            // Extract the text from the response
            String extractedText = responseJson.get("choices").get(0).get("message").asText();

         // Get the assistant's message content
            String assistantContent = responseJson.path("choices").get(0).path("message").path("content").asText();
            // Process the extracted text as needed
            // For example, you may want to convert it to a JSON object if it's in JSON format
            JsonNode extractedData = mapper.readTree(assistantContent);
            JsonNode detail =  extractedData.get("Details");
            in.setNoFacture(extractedData.get("noFacture").asInt());
            in.setAmount(extractedData.get("amount").asDouble());
            in.setTps(extractedData.get("tps").asDouble());
            in.setTvq(extractedData.get("tvq").asDouble());
            in.setRecipient(extractedData.get("recipient").asText());
            in.setOrigine(extractedData.get("origine").asText());
            JsonNode description = extractedData.get("description");
            StringBuilder combinedString = new StringBuilder();
            
         
            for (JsonNode element : description) {
                // Append the string value of the element, followed by a newline
                combinedString.append(element.asText()).append("\n");
            }
            in.setDescription(combinedString.toString());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = extractedData.get("date").asText();
            try {
				in.setDate(formatter.parse(dateStr));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			System.out.println(extractedData);
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return ResponseEntity.ok(in);
	}
	
	public String extractTextFromPDF(MultipartFile file) throws IOException {
	    // Convert the MultipartFile to an InputStream
	    try (InputStream inputStream = file.getInputStream()) {
	        // Load the PDF document
	        PDDocument document = PDDocument.load(inputStream);

	        // Create a PDFTextStripper to extract the text
	        PDFTextStripper pdfStripper = new PDFTextStripper();

	        // Get the text from the document
	        String text = pdfStripper.getText(document);

	        // Close the document
	        document.close();

	        // Do something with the text, e.g., parse the invoice details
	        // ...

	        return text;
	    }
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
		in.setDescription(request.getDescription());
		in.setRecipient(request.getRecipient());
		//in.setOrigine(request.g);
		java.sql.Date sqlDate = java.sql.Date.valueOf(request.getDate());
		Date utilDate = new Date(sqlDate.getTime());
		in.setDate(utilDate);
	
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
