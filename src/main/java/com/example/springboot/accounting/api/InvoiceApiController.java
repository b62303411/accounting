package com.example.springboot.accounting.api;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.accounting.model.dto.InvoiceCreationRequest;
import com.example.springboot.accounting.model.dto.InvoiceDto;
import com.example.springboot.accounting.model.dto.InvoiceUpdateRequest;
import com.example.springboot.accounting.model.entities.Attachment;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.repository.InvoiceRepository;
import com.example.springboot.accounting.service.AttachmentService;
import com.example.springboot.accounting.service.DateParser;
import com.example.springboot.accounting.service.InvoiceService;
import com.example.springboot.accounting.service.OpenAiRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceApiController {

	private AttachmentService attachmentService;
	private InvoiceRepository invoiceRepo;
	private InvoiceService invoiceService;
	private OpenAiRequestService service;
	private DateParser dateParser;

	@Autowired
	public InvoiceApiController(DateParser dateParser,AttachmentService attachmentService, InvoiceRepository invoiceRepo,
			InvoiceService invoiceService, OpenAiRequestService service) {
		this.invoiceRepo = invoiceRepo;
		this.invoiceService = invoiceService;
		this.service = service;
		this.attachmentService = attachmentService;
		this.dateParser=dateParser;
	}

	boolean isComplete(Invoice in) 
	{
		return (in.getNoFacture() != null && in.getOrigine() !=null && in.getAmount()!=null && in.getRecipient()!=null && in.getDescription()!=null);
	}
	
	
	@PostMapping("/parse")
	ResponseEntity<Invoice> parse(@RequestParam("file") MultipartFile file) {
		Invoice invoice = new Invoice();
		String context="";
		for(int i = 0; i<5;i++) 
		{
			Invoice in = new Invoice();
			try {
				populateInvoice(file, in,context);
				if(null==invoice.getNoFacture() && null != in.getNoFacture()) 
				{
					invoice.setNoFacture(in.getNoFacture());					
				}
				
				if(null != in.getOrigine() && !in.getOrigine().isBlank() && !in.getOrigine().isEmpty())
				{
					invoice.setOrigine(in.getOrigine());
				}
				
				if(null != in.getRecipient() && !in.getRecipient().isBlank()) 
				{
					invoice.setRecipient(in.getRecipient());
				}
				
				if(null != in.getAmount())
				{
					invoice.setAmount(in.getAmount());
				}
				
				if(null != in.getTps()) 
				{
					invoice.setTps(in.getTps());
				}
				
				if(null != in.getTvq()) 
				{
					invoice.setTvq(in.getTvq());
				}
				
				if(invoice.getDate() ==null && null != in.getDate()) 
				{
					invoice.setDate(in.getDate());
				}
				
				if(null != in.getDescription() && !in.getDescription().isEmpty()) 
				{
					invoice.setDescription(in.getDescription());
				}
				
				if(isComplete(invoice))
					return ResponseEntity.ok(invoice);
				  // Create a new ObjectMapper
	            ObjectMapper objectMapper = new ObjectMapper();

	            // Convert the object to a JSON string
	            String jsonString = objectMapper.writeValueAsString(invoice);
	            
				context = "The values that are not null are valid:" + jsonString;

				
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		

		return ResponseEntity.ok(invoice);
	}

	private JsonNode populateInvoice(MultipartFile file, Invoice in,String extraContext)
			throws IOException, JsonProcessingException, JsonMappingException {
		String text = extractTextFromPDF(file);
		
		String responseString = service.submitInvoiceQuery(text,extraContext);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode responseJson = mapper.readTree(responseString);

		// Check for errors
		if (responseJson.has("error")) {
			String errorMessage = responseJson.get("error").get("message").asText();
			System.out.println("Error from OpenAI: " + errorMessage);
			return null;
		}
		// Get the assistant's message content
		String assistantContent = responseJson.path("choices").get(0).path("message").path("content").asText();
		// Process the extracted text as needed
		// For example, you may want to convert it to a JSON object if it's in JSON
		// format
		JsonNode extractedData = mapper.readTree(assistantContent);
		
		if (extractedData.has("noFacture") && !extractedData.get("noFacture").isNull()) {
		    in.setNoFacture(extractedData.get("noFacture").asText());
		}

		if (extractedData.has("amount") && !extractedData.get("amount").isNull()) {
		    in.setAmount(extractedData.get("amount").asDouble());
		}

		if (extractedData.has("tps") && !extractedData.get("tps").isNull()) {
		    in.setTps(extractedData.get("tps").asDouble());
		}

		if (extractedData.has("tvq") && !extractedData.get("tvq").isNull()) {
		    in.setTvq(extractedData.get("tvq").asDouble());
		}

		if (extractedData.has("recipient") && !extractedData.get("recipient").isNull()) {
		    in.setRecipient(extractedData.get("recipient").asText());
		}

		if (extractedData.has("origine") && !extractedData.get("origine").isNull()) {
		    in.setOrigine(extractedData.get("origine").asText());
		}
		JsonNode descriptionNode = extractedData.get("description");
		
		if (descriptionNode != null && !descriptionNode.isArray()) {
		    String description = descriptionNode.asText(); // or other appropriate type
		    in.setDescription(description);
		}
		else if(descriptionNode != null && descriptionNode.isArray()) 
		{
			StringBuilder combinedString = new StringBuilder();
			for (JsonNode element : descriptionNode) {
				// Append the string value of the element, followed by a newline
				combinedString.append(element.asText()).append("\n");
			}
			in.setDescription(combinedString.toString());
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = extractedData.get("date").asText();
		Date date = dateParser.parseDate(dateStr);
		in.setDate(date);
		
			
		
		System.out.println(extractedData);
		return extractedData;
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

		Attachment att = null;
		try {
			byte[] bytes = request.getFile().getBytes();
			String contentType = request.getFile().getContentType();
			att = attachmentService.createAttachment(bytes, contentType);
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
		in.setOrigine(request.getOrigine());
		java.sql.Date sqlDate = java.sql.Date.valueOf(request.getDate());
		Date utilDate = new Date(sqlDate.getTime());
		in.setDate(utilDate);

	}

	@PostMapping("/updateBill")
	public ResponseEntity<Invoice> updateBill(@RequestBody InvoiceUpdateRequest request) {
		Optional<Invoice> invoice = invoiceRepo.findById(request.getId());
		if (invoice.isPresent()) {
			fill(request, invoice.get());
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

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
		try {
			invoiceService.deleteInvoice(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
