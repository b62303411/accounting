package com.example.springboot.accounting.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.model.TransactionType;
import com.example.springboot.accounting.model.dto.Answer;
import com.example.springboot.accounting.model.dto.FinanceEntry;
import com.example.springboot.accounting.model.dto.SelectedRowDTO;
import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.model.entities.Consolidation;
import com.example.springboot.accounting.model.entities.ExploitationExpense;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.model.entities.Supplier;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.ConsolidationRepository;
import com.example.springboot.accounting.service.AssetService;
import com.example.springboot.accounting.service.ExpensesService;
import com.example.springboot.accounting.service.InvoiceService;
import com.example.springboot.accounting.service.OpenAiRequestService;
import com.example.springboot.accounting.service.SupplierService;
import com.example.springboot.accounting.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierApiController {

	@Autowired
	private ConsolidationRepository consolidations;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private SupplierService contactService;

	@Autowired
	private ExpensesService expenseService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private OpenAiRequestService ai;

	@Autowired
	private AssetService assetService;

	@GetMapping
	public ResponseEntity<List<Supplier>> getAllContacts() {
		return ResponseEntity.ok(contactService.getAllSuppliers());
	}

	@PostMapping("/reconcile")
	public void reconcile(@RequestBody List<SelectedRowDTO> selectedRows) {

		Transaction transaction = null;
		Invoice invoice = null;
		ExploitationExpense expense = null;
		Consolidation c = null;
		for (SelectedRowDTO selectedRowDTO : selectedRows) {
			switch (selectedRowDTO.getType()) {
			case "Transaction":
				transaction = transactionService.findById(selectedRowDTO.getId());

				break;
			case "Invoice":
				invoice = invoiceService.findById(selectedRowDTO.getId());
				break;
			case "Expense":
				expense = expenseService.findById(selectedRowDTO.getId());
				break;
			}

			System.out.println();
		}
		if (null == expense && null != invoice && null != transaction) {
			c = consolidations.findByTransactionId(transaction.getId());
			if (null == c) {
				c = new Consolidation();
			}
			c.setInvoice(invoice);
			c.setTransaction(transaction);
			c.setSupplier(invoice.getOrigine());
			if (transaction.getType() == TransactionType.OperatingExpenses) {
				expense = expenseService.findByTransaction(transaction);
				expense.setInvoice(invoice);
				expense.setPayee(invoice.getOrigine());
				expense.setTransaction(transaction);
				c.setEpense(expense);
				expenseService.save(expense);

			}
			if (transaction.getType() == TransactionType.AssetPurchased) {

				Asset asset = assetService.findByTransaction(transaction);
				c.setAsset(asset);
				c.setTransaction(transaction);
				c.setInvoice(invoice);
				c.setSupplier(transaction.getPayee());
				consolidations.save(c);
				return;

			}
			//
			//
			//

			consolidations.save(c);
		}
		if (null != invoice && null != expense) {
			c = consolidations.findByInvoiceId(invoice.getId());
			if (null == c) {
				c = new Consolidation();
			}
			c.setInvoice(invoice);
			c.setEpense(expense);
			c.setSupplier(invoice.getOrigine());
			consolidations.save(c);
			expense.setInvoice(invoice);

			expenseService.save(expense);
		}
	}

	@PostMapping
	public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
		return ResponseEntity.ok(contactService.createSupplier(supplier));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Supplier> getContact(@PathVariable Long id) {
		return ResponseEntity.ok(contactService.getSupplierById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Supplier> updateContact(@PathVariable Long id, @RequestBody Supplier supplier) {
		try {
			return ResponseEntity.ok(contactService.updateSupplier(id, supplier));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
		contactService.deleteSupplier(id);
		return ResponseEntity.noContent().build();
	}

	public boolean isConsolidated(Transaction transaction) {
		Consolidation t = consolidations.findByTransactionId(transaction.getId());
		return (null != t);
	}

	public boolean isConsolidated(ExploitationExpense e) {
		Consolidation t = consolidations.findByEpenseId(e.getId());
		return (null != t);
	}

	public boolean isConsolidated(Invoice e) {
		Consolidation t = consolidations.findByInvoiceId(e.getId());
		return (null != t);
	}

	@GetMapping("/{id}/consolidated")
	public List<Consolidation> getSupplierConsolidatedEntry(@PathVariable Long id) {
		Supplier supplier = contactService.findById(id);
		return consolidations.findAllBySupplier(supplier.getName());
	}

	@GetMapping("/{id}/entries")
	public List<FinanceEntry> getSupplierEntry(@PathVariable Long id) {

		List<ExploitationExpense> allEx = expenseService.findAll();
		List<Supplier> suppliers = contactService.getAllSuppliers();
		Map<String, String> names = new HashMap();
		for (Supplier s : suppliers) {
			names.put(s.getName(), s.getServiceType());
		}

		for (ExploitationExpense exploitationExpense : allEx) {
			if (exploitationExpense.getPayee() != null) {
				if (!names.keySet().contains(exploitationExpense.getPayee())) {
					String approx = ai.guessSupplier(names, exploitationExpense.getPayee());
					System.out.println(approx);
					for (Supplier sup : suppliers) {
						if (!isConsolidated(exploitationExpense)) {
							int distance = StringUtils.getLevenshteinDistance(sup.getName().toUpperCase(),
									exploitationExpense.getPayee().toUpperCase());
							if (distance < 10 && !exploitationExpense.getPayee().equals(sup.getName())) {

								System.out.println(sup.getName() + " VS " + exploitationExpense.getPayee());
							}
							System.out.println("Levenshtein Distance: " + distance);
						}
					}
				}

			}
		}

		List<FinanceEntry> fin = new ArrayList<FinanceEntry>();
		Supplier supplier = contactService.findById(id);
		List<ExploitationExpense> ex = expenseService.findAllByPayee(supplier.getName());
		List<Consolidation> consolidation = consolidations.findAllBySupplier(supplier.getName());
		for (ExploitationExpense exploitationExpense : ex) {
			if (!isConsolidated(exploitationExpense))
				createEntry(fin, exploitationExpense);

		}

		List<Invoice> invoices = invoiceService.findAllByOrigin(supplier.getName());
		for (Invoice invoice : invoices) {
			if (!isConsolidated(invoice))
				createEntry(fin, invoice);
		}

		List<Transaction> transactions = transactionService.findAllByPayee(supplier.getName());
		for (Transaction transaction : transactions) {
			if (!isConsolidated(transaction))
				createEntry(fin, transaction);
		}

		Collections.sort(fin, new Comparator<FinanceEntry>() {
			public int compare(FinanceEntry p1, FinanceEntry p2) {
				return p1.getDate().compareTo(p2.getDate());
			}
		});
		return fin;
	}

	private void createEntry(List<FinanceEntry> fin, Transaction transaction) {
		FinanceEntry entry = new FinanceEntry();
		entry.setDate(transaction.getDate());
		entry.setType("Transaction");
		entry.setId(transaction.getId().toString());
		entry.setAmount("" + transaction.getAmount());

		if (entry.getDate() == null) {
			System.err.println();
		}
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonString = objectMapper.writeValueAsString(transaction);
			entry.setContent(jsonString);
			fin.add(entry);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private void createEntry(List<FinanceEntry> fin, Invoice invoice) {
		FinanceEntry entry = new FinanceEntry();
		entry.setDate(invoice.getDate());
		entry.setType("Invoice");
		entry.setId(invoice.getId().toString());
		entry.setAmount("" + invoice.getAmount());
		if (entry.getDate() == null) {
			System.err.println();
		}
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonString = objectMapper.writeValueAsString(invoice);
			entry.setContent(jsonString);
			fin.add(entry);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private void createEntry(List<FinanceEntry> fin, ExploitationExpense exploitationExpense) {
		FinanceEntry entry = new FinanceEntry();
		entry.setDate(exploitationExpense.getDate());
		entry.setType("Expense");
		entry.setId(exploitationExpense.getId().toString());
		entry.setAmount("" + exploitationExpense.getTotal());
		if (exploitationExpense.getDate() == null) {
			System.err.println();
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonString = objectMapper.writeValueAsString(exploitationExpense);
				entry.setContent(jsonString);
				fin.add(entry);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	public String generateFrom(Map<String, String> names) {
		List<Transaction> list = transactionService.findAllNullPayee();
		for (Transaction transaction : list) {
			String responseString = ai.guessSupplier(names, transaction.getNote() + "," + transaction.getDescription()
					+ "," + transaction.getTransactionNature() + "," + transaction.getType());
			System.out.println(responseString);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode responseJson;
			try {
				responseJson = mapper.readTree(responseString);
				// Check for errors
				if (responseJson.has("error")) {
					String errorMessage = responseJson.get("error").get("message").asText();
					System.out.println("Error from OpenAI: " + errorMessage);
					return null;
				}
				// Get the assistant's message content
				String assistantContent = responseJson.path("choices").get(0).path("message").path("content").asText();
				ObjectMapper objectMapper = new ObjectMapper();
				Answer answerObj = objectMapper.readValue(assistantContent, Answer.class);
				transaction.setPayee(answerObj.answer);
				transactionService.save(transaction);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		transactionService.saveAll(list);
		return null;
	}

}
