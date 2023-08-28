package com.example.springboot.accounting.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.accounting.model.AiFileResult;
import com.example.springboot.accounting.model.CollisionSet;
import com.example.springboot.accounting.model.TransactionNature;
import com.example.springboot.accounting.model.TransactionType;
import com.example.springboot.accounting.model.dto.CreditCardActivity;
import com.example.springboot.accounting.model.dto.ManualTransactionRequest;
import com.example.springboot.accounting.model.dto.TransactionAddAttachmentRequest;
import com.example.springboot.accounting.model.dto.TransactionDTO;
import com.example.springboot.accounting.model.dto.TransactionRequest;
import com.example.springboot.accounting.model.entities.Account;
import com.example.springboot.accounting.model.entities.Attachment;
import com.example.springboot.accounting.model.entities.BankReccord;
import com.example.springboot.accounting.model.entities.BankStatement;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.AccountRepository;
import com.example.springboot.accounting.service.BankReccordService;
import com.example.springboot.accounting.service.BankStatementPromptFactory;
import com.example.springboot.accounting.service.DataParsingService;
import com.example.springboot.accounting.service.PdfService;
import com.example.springboot.accounting.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class TransactionApiController {
	Map<String, String> monthConversion = new HashMap<>();
	private BankStatementPromptFactory pspf;
	private final TransactionService transactionService;
	private final BankReccordService bankReccordService;
	private final AccountRepository accountRepo;
	private final PdfService pdfService;
	private final DataParsingService dataParsingService;
	@Autowired
	public TransactionApiController(DataParsingService dataParsingService,BankStatementPromptFactory pspf, AccountRepository accountRepo,
			TransactionService transactionService, BankReccordService bankReccordService, PdfService pdfService) {
		super();
		this.pdfService = pdfService;
		this.pspf = pspf;
		this.transactionService = transactionService;
		this.bankReccordService = bankReccordService;
		this.accountRepo = accountRepo;
		this.dataParsingService=dataParsingService;
		monthConversion.put("Jan", "Jan");
		monthConversion.put("Fév", "Feb");
		monthConversion.put("Mar", "Mar");
		monthConversion.put("Avr", "Apr");
		monthConversion.put("Mai", "May");
		monthConversion.put("Juin", "Jun");
		monthConversion.put("Juil", "Jul");
		monthConversion.put("Août", "Aug");
		monthConversion.put("Sept", "Sep");
		monthConversion.put("Oct", "Oct");
		monthConversion.put("Nov", "Nov");
		monthConversion.put("Déc", "Dec");

		monthConversion.put("janv", "Jan");
		monthConversion.put("févr", "Feb");
		monthConversion.put("mars", "Mar");
		monthConversion.put("avr", "Apr");
		monthConversion.put("mai", "May");
		monthConversion.put("juin", "Jun");
		monthConversion.put("juil", "Jul");
		monthConversion.put("août", "Aug");
		monthConversion.put("sept", "Sep");
		monthConversion.put("oct", "Oct");
		monthConversion.put("nov", "Nov");
		monthConversion.put("déc", "Dec");

		monthConversion.put("JAN", "Jan");
		monthConversion.put("FEV", "Feb");
		monthConversion.put("MAR", "Mar");
		monthConversion.put("AVR", "Apr");
		monthConversion.put("MAI", "May");
		monthConversion.put("JUN", "Jun");
		monthConversion.put("JUL", "Jul");
		monthConversion.put("AOU", "Aug");
		monthConversion.put("SEP", "Sep");
		monthConversion.put("OCT", "Oct");
		monthConversion.put("NOV", "Nov");
		monthConversion.put("DEC", "Dec");
	}

	@PostMapping("/upload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
		// Process the file here
		try {
			InputStream inputStream = file.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String header = bufferedReader.readLine();
			String line;
			int line_count = 0;
			while ((line = bufferedReader.readLine()) != null) {
				Transaction transaction = parseString(line);
				try {
					transactionService.save(transaction);
					line_count++;
				} catch (Exception e) {
					System.err.println();
				}

			}
			System.out.println("Added:" + line_count);
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error occurred while processing the file.", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("File processed successfully.", HttpStatus.OK);
	}

	@PostMapping("/api/parse-bank-statement")
	public ResponseEntity<Map<String, Object>> parseBankStatement(@RequestParam("file") MultipartFile file) {
		Map<String, Object> results = new HashMap<String, Object>();

		try {
			AiFileResult r = populateTransaction(file, "");

			return ResponseEntity.ok(results);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok(results);
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
	private AiFileResult populateTransaction(MultipartFile file, String extraContext)
			throws IOException, JsonProcessingException, JsonMappingException {
		String text = pdfService.extractTextFromPDF(file);

		AiFileResult response = this.pspf.submitBankStatementQuery(text, extraContext);

		JsonNode extractedData = response.firstMetaData.answer;
		JsonNode AccountInfo = extractedData.get("AccountInfo");
		JsonNode StatementPeriod = AccountInfo.get("StatementPeriod");
		String[] dateParts = StatementPeriod.textValue().split("-"); // split by " - "
		Date start = null;
		if (dateParts.length == 2) {
			String[] here = dateParts[0].split(" ");
			start = dataParsingService.parseDate(here);

		} else {
			System.err.println("Invalid date string format");
			return null;

		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		int year = calendar.get(Calendar.YEAR);
		if (extractedData.has("Transactions") && !extractedData.get("Transactions").isNull()) {
			for (final JsonNode objNode : extractedData.get("Transactions")) {
				// Do something with each individual object in the array
				String Retraits = objNode.get("Retraits").asText();
				String Depots = objNode.get("Depots").asText();
				String date = objNode.get("Date").asText();
				String description = objNode.get("Description").asText();
				String day_str = date.substring(0, 2);
				String month_str = date.substring(2, 5);
				String month = monthConversion.get(month_str);
				String Solde = objNode.get("Solde").asText();
				Double sold = dataParsingService.parseDouble(Solde);
				Double retrait = dataParsingService.parseDouble(Retraits);
				Double depot = dataParsingService.parseDouble(Depots);
				Double amount = (retrait == null) ? depot : retrait;
				if (null != amount) {
					Date date_transaction = dataParsingService.parseDate(day_str, month, year);
					List<Transaction> list = transactionService.findAllByDateAndAmount(date_transaction, amount);
					for (Transaction t : list) {
						t.setSolde(sold);
						transactionService.save(t);
						System.out.println(t.getId() + "");
					}
					if (list.isEmpty() && !description.contains("SOLDE REPORTE")
							&& (depot != null && depot != 0.0 || retrait != null && retrait != 0.0)) {
						Transaction t = new Transaction();
						t.setAmount(amount);
						t.setDescription(description);
						if (depot != null) {
							t.setTransactionNature(TransactionNature.Debit);
						} else {
							t.setTransactionNature(TransactionNature.Credit);
						}
						t.setDate(date_transaction);
						transactionService.save(t);

					}
				}
				System.err.println(objNode.toString());
			}
			// JSONArray array = extractedData.("Transactions").
			// in.setAmount(extractedData.get("amount").asDouble());

		}
		return response;
	}


	

	public Transaction parseString(String line) {
		String[] fields = line.split(",");
		Transaction transaction = new Transaction();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			transaction.setDate(dateFormat.parse(fields[0]));
		} catch (ParseException e) {
			e.printStackTrace();

		}

		transaction.setDescription(fields[1]);
		switch (fields[2]) {
		case "Restaurants":
			transaction.setType(TransactionType.OperatingExpenses);
			break;
		default:
			transaction.setType(TransactionType.Unknown);
			break;

		}

		// transaction.setType(null);
		try {
			transaction.setAmount(Double.parseDouble(fields[3].replace("-", "").replace("$", "")));
		} catch (Exception e) {
			System.err.println();
		}
		if (transaction.getAmount() > 0) {
			transaction.setTransactionNature(TransactionNature.Credit);
		} else {
			transaction.setTransactionNature(TransactionNature.Debit);
		}
		transaction.setAccount(fields[5]);

		return transaction;
	}

	public Double convertDouble(String numberString) {
		numberString = numberString.replace(" ", "").replace(",", ".");
		return Double.parseDouble(numberString);
	}

	

	private Double getAmmount(TransactionRequest transaction) {
		String depots = transaction.getDEPOTS();
		String retrait = transaction.getRETRAITS();
		if (null != depots && !depots.isBlank()) {
			return convertDouble(depots);
		} else if (null != retrait && !retrait.isBlank()) {
			return -convertDouble(retrait);
		}
		return null;
	}

	/**
	 * 
	 * @param year
	 * @param date_str
	 * @return
	 */
	public Map<String, LocalDate> getBoundaries(String year, String date_str) {
		Map<String, LocalDate> boundaries = new HashMap<>();

		Date date_ = dataParsingService.getDate(date_str, year);
		LocalDate date = dataParsingService.getLocalDateFromDate(date_);
		LocalDate startBoundary = date.minusMonths(1).withDayOfMonth(date.minusMonths(1).lengthOfMonth());
		LocalDate endBoundary = date.withDayOfMonth(date.lengthOfMonth());
		boundaries.put("start", startBoundary);
		boundaries.put("end", endBoundary);
		return boundaries;

	}
	
 
	
	@PostMapping("/api/transactions/eliminate-duplicates")
	public ResponseEntity<Object> fixDuplicateTransactions()
	{
		transactionService.fixDuplicate();
		return ResponseEntity.ok(new String());
	}
	

	@PostMapping("/addCreditCardTransaction")
	public ResponseEntity<Object> addCreditCardTransaction(@RequestBody CreditCardActivity activity) {
		// process activity object
		if (activity.getDateOperation().isEmpty())
			return ResponseEntity.noContent().build();
		Transaction transaction = new Transaction();
		BankReccord br = new BankReccord();
		BankStatement bs = new BankStatement();
		bs.setAcc(activity.getAcc());
		bs.setAccountName(activity.getAccount_name());
		String operation_date_str = activity.getDateOperation();
		Map<String, LocalDate> b = getBoundaries(activity.getYear(), operation_date_str);
		bs.setFrom(dataParsingService.formatLocalDate(b.get("start")));
		bs.setTo(dataParsingService.formatLocalDate(b.get("end")));
		bs.setYear(Integer.parseInt(activity.getYear()));
		// bs.setSuc(transaction.getSuc());
		bankReccordService.save(bs);
		transaction.setAccount(activity.getAcc());
		Double amount = dataParsingService.currencyToDouble(activity.getMontant());
		// Remove currency symbol and thousands separators
		transaction.setAmount(amount);
		Date date = dataParsingService.getDate(activity.getDateActivite(), activity.getYear());
		transaction.setDate(date);
		transaction.setDescription(activity.getDescription());
		transactionService.save(transaction);
		return ResponseEntity.ok().build();
	}

	private void addAmountToTransaction(String cleanedAmountString, Transaction transaction) {
		try {
			double amount = Double.parseDouble(cleanedAmountString);
			transaction.setAmount(amount);
			System.out.println("Parsed amount: " + amount);
		} catch (NumberFormatException e) {
			System.out.println("Failed to parse amount: " + e.getMessage());
		}
	}

	@PostMapping("/addLegacyTransaction")
	public ResponseEntity<Object> addLegacyTransaction(@RequestBody TransactionDTO transactionRequest) {
		Optional<Account> account = accountRepo
				.findByAccountName(transactionRequest.getAccountName().replace(" ", "_"));
		Transaction transaction = new Transaction();
		transaction.setAccount(account.get().getAccountNo());
		transaction.setAmount(transactionRequest.getAmount());
		transaction.setDescription(transactionRequest.getOriginalDescription());

		// Define the formatter based on the format of your date string.
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

		// Parse the string to a LocalDate object.
		LocalDate localDate = LocalDate.parse(transactionRequest.getDate(), formatter);

		Date date = java.sql.Date.valueOf(localDate);

		transaction.setDate(date);
		// transaction.setType(TransactionType.valueOf(transactionRequest.getTransactionType()));
		transactionRequest.getAccountName();
		transaction.setNote(transactionRequest.getLabels());
		transactionService.save(transaction);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/addManualTransaction")
	public ResponseEntity<Object> addManualTransaction(@RequestBody ManualTransactionRequest transactionRequest) {
		Transaction transaction = new Transaction();
		transaction.setAccount(transactionRequest.getAccount());
		transaction.setDescription(transactionRequest.getDescription());

		// Remove currency symbol and thousands separators
		String cleanedAmountString = dataParsingService.currencyToAmount(transactionRequest.getAmount());

		try {
			double amount = Double.parseDouble(cleanedAmountString);
			transaction.setAmount(amount);
			System.out.println("Parsed amount: " + amount);
		} catch (NumberFormatException e) {
			System.out.println("Failed to parse amount: " + e.getMessage());
		}

		String dateFormat = "dd/MM/yyyy";

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		try {
			Date date = formatter.parse(transactionRequest.getDate());
			transaction.setDate(date);
			System.out.println("Parsed date: " + date);
		} catch (ParseException e) {
			System.out.println("Failed to parse date: " + e.getMessage());
		}
		transactionService.save(transaction);
		return ResponseEntity.ok().build();
	}


	@PostMapping("/addTransaction")
	public void addTransaction(@RequestBody TransactionRequest transaction) {

		Transaction t = null;
		BankReccord br = new BankReccord();
		BankStatement bs = new BankStatement();
		bs.setAcc(transaction.getAcc());
		bs.setFrom(transaction.getDate_from());
		bs.setTo(transaction.getDate_to());
		bs.setYear(Integer.parseInt(transaction.getYear()));
		bs.setSuc(transaction.getSuc());

		bankReccordService.save(bs);

		if (transaction.getDATE().isEmpty() || transaction.getDESCRIPTION() == null)
			return;
		Date date = dataParsingService.getDate(transaction.getDATE(), transaction.getYear());
		Double ammount = getAmmount(transaction);
		if(transaction.SOLDE!=null && !transaction.SOLDE.isEmpty())
			br.setSolde(convertDouble(transaction.SOLDE));
		br.setAcc(transaction.getAcc());
		br.setDescription(transaction.getDESCRIPTION());
		br.setSuc(transaction.getSuc());
		br.setDate(date);
	    // Create a Calendar object and set its time to the Date object
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Extract the day of the month from the Calendar object
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        if(day==31 && month == 7 && transaction.getDESCRIPTION().contains("FRAIS MENS PLAN SERV")) 
        {
        	System.err.println();
        }
		if (null != ammount) {
			t = new Transaction();
			t.setDescription(transaction.getDESCRIPTION());
			t.setDate(date);
			t.setAmount(Math.abs(ammount));
			if (transaction.getRETRAITS() != null && !transaction.getRETRAITS().isEmpty()) {
				t.setTransactionNature(TransactionNature.Debit);
			} else {
				t.setTransactionNature(TransactionNature.Credit);
			}
		
			t.setAccount(transaction.getAcc());
			Double s = dataParsingService.parseDouble(transaction.getSOLDE());
			t.setSolde(s);
			Transaction saved = transactionService.save(t);
			try {
				br.setTransaction(saved);
			} catch (Exception e) {

			}
		}

		bankReccordService.save(br);
	}

	@PostMapping("/updateTransactionType")
	public void updateTransactionType(@RequestParam("id") Long id, @RequestParam("type") TransactionType type,
			@RequestParam("applyToAll") boolean applyToAll) {
		Transaction transaction = transactionService.findById(id);
		transaction.setType(type);
		if (applyToAll) {
			List<Transaction> transactions = transactionService.findAllByDescription(transaction.getDescription());
			for (Transaction t : transactions) {
				t.setType(type);

			}
			transactionService.saveAll(transactions);

		}
		transactionService.save(transaction);
	}

	@PutMapping("transaction/{id}/updateNote")
	public ResponseEntity<?> updateNote(@PathVariable("id") Long id, @RequestParam("note") String note) {
		// Your logic to update the note of the transaction goes here
		Transaction transaction = transactionService.findById(id);
		transaction.setNote(note);
		transactionService.save(transaction);
		return ResponseEntity.ok().build(); // Respond with 200 OK status
	}

	// api/transaction/2657/addAttachment
	@PostMapping("/api/transaction/addAttachment")
	public ResponseEntity<Object> addAttachment(@ModelAttribute TransactionAddAttachmentRequest req) {
		Transaction transaction = transactionService.findById(req.getId());
		Attachment att = new Attachment();
		try {
			att.setFile(req.getFile().getBytes());
			transaction.addAttachment(att);
			transactionService.save(transaction);
		} catch (IOException e) {

			e.printStackTrace();
		}

		return ResponseEntity.ok().build();
	}

}
