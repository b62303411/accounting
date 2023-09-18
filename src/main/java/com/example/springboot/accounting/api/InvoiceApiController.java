package com.example.springboot.accounting.api;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.accounting.model.AiFileResult;
import com.example.springboot.accounting.model.dto.InvoiceCreationRequest;
import com.example.springboot.accounting.model.dto.InvoiceDto;
import com.example.springboot.accounting.model.dto.InvoiceUpdateRequest;
import com.example.springboot.accounting.model.entities.Attachment;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.repository.InvoiceRepository;
import com.example.springboot.accounting.service.AttachmentService;
import com.example.springboot.accounting.service.InvoicePromptFactory;
import com.example.springboot.accounting.service.InvoiceService;
import com.example.springboot.accounting.service.util.DateParser;
import com.example.springboot.accounting.service.util.PdfService;
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
	//private OpenAiRequestService service;
	private InvoicePromptFactory ipf;
	private final PdfService pdfService;

	private DateParser dateParser;
	private HashMap<String, AiFileResult> aiResults;

	@Autowired
	public InvoiceApiController(
			DateParser dateParser, 
			AttachmentService attachmentService,
			InvoiceRepository invoiceRepo, 
			InvoiceService invoiceService, 
			//OpenAiRequestService service,
			InvoicePromptFactory ipf,
			PdfService pdfService) {
		this.invoiceRepo = invoiceRepo;
		this.invoiceService = invoiceService;
		this.pdfService = pdfService;
		this.attachmentService = attachmentService;
		this.dateParser = dateParser;
		this.ipf = ipf;

		aiResults = new HashMap<String, AiFileResult>();
	}

	boolean isComplete(Invoice in) {
		return (in.getNoFacture() != null && in.getOrigine() != null && in.getAmount() != null
				&& in.getRecipient() != null && in.getDescription() != null);
	}

	@PostMapping("/parse")
	ResponseEntity<Map<String, Object>> parse(@RequestParam("file") MultipartFile file) {
		Invoice invoice = new Invoice();
		String context = "";
		Map<String, Object> results = new HashMap<String, Object>();
		for (int i = 0; i < 5; i++) {
			Invoice in = new Invoice();
			try {

				String hash = attachmentService.getSHA256Hash(file.getBytes());
				AiFileResult result = null;
				if (results.containsKey(hash)) {
					result = aiResults.get(hash);
				} else {
					result = populateInvoice(file, in, context);
					if (result.finalResult != null)
						aiResults.put(hash, result);
				}

				if (result.finalResult != null) {

					List<Invoice> list = invoiceRepo.findAllByNoFacture(in.getNoFacture());
					if (list.size() > 1)
						resolveDuplicate(list);
					Invoice existing = invoiceRepo.findByNoFacture(in.getNoFacture());
					results.put("factureNo", in.getNoFacture());
					results.put("meta1", result.firstMetaData.answer.toPrettyString());
					results.put("meta2", result.finalResult.answer.toPrettyString());
					if (null != existing) {
						results.put("invoiceExist", existing);
					}

					if (null == invoice.getNoFacture() && null != in.getNoFacture()) {
						invoice.setNoFacture(in.getNoFacture());
					}

					if (null != in.getOrigine() && !in.getOrigine().isBlank() && !in.getOrigine().isEmpty()) {
						invoice.setOrigine(in.getOrigine());
					}

					if (null != in.getRecipient() && !in.getRecipient().isBlank()) {
						invoice.setRecipient(in.getRecipient());
					}

					if (null != in.getAmount()) {
						invoice.setAmount(in.getAmount());
					}

					if (null != in.getTps()) {
						invoice.setTps(in.getTps());
					}

					if (null != in.getTvq()) {
						invoice.setTvq(in.getTvq());
					}

					if (invoice.getDate() == null && null != in.getDate()) {
						invoice.setDate(in.getDate());
					}

					if (null != in.getDescription() && !in.getDescription().isEmpty()) {
						invoice.setDescription(in.getDescription());
					}

					results.put("Invoice", invoice);

					if (isComplete(invoice))
						return ResponseEntity.ok(results);
					// Create a new ObjectMapper
					ObjectMapper objectMapper = new ObjectMapper();

					// Convert the object to a JSON string
					String jsonString = objectMapper.writeValueAsString(invoice);

					context = "The values that are not null are valid:" + jsonString;
				}

			} catch (IOException e) {

				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ResponseEntity.ok(results);
	}

	private void resolveDuplicate(List<Invoice> list) {
		for (Invoice invoice : list) {
			System.err.println(invoice.getId());
		}
		invoiceRepo.delete(list.get(0));

	}
	
	
	
	
	/**
	 * 
	 * @param file
	 * @param in
	 * @param extraContext
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	private AiFileResult populateInvoice(MultipartFile file, Invoice in, String extraContext)
			throws IOException, JsonProcessingException, JsonMappingException {
		String text =pdfService.extractTextFromPDF(file);

		AiFileResult response = this.ipf.submitInvoiceQuery(text, extraContext);
		if (null == response || response.finalResult == null)
			return response;
		JsonNode extractedData = response.finalResult.answer;

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
		} else if (descriptionNode != null && descriptionNode.isArray()) {
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
		return response;
	}

	

	@PostMapping("/create")
	ResponseEntity<Invoice> create(@ModelAttribute InvoiceCreationRequest request) {
		Invoice in = new Invoice();
		fill(request, in);
		Invoice invoice = invoiceRepo.findByNoFacture(in.getNoFacture());
		if (null != invoice)
			return ResponseEntity.badRequest().build();
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
		String dateString = request.getDate();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			Date date = format.parse(dateString);
			System.out.println("Parsed Date: " + date);
			in.setDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

//		java.sql.Date sqlDate = java.sql.Date.valueOf(request.getDate());
//		Date utilDate = new Date(sqlDate.getTime());

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
