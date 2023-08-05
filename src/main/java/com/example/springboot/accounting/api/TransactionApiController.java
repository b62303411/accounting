package com.example.springboot.accounting.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.accounting.model.TransactionType;
import com.example.springboot.accounting.model.dto.CreditCardActivity;
import com.example.springboot.accounting.model.dto.ManualTransactionRequest;
import com.example.springboot.accounting.model.dto.TransactionDTO;
import com.example.springboot.accounting.model.dto.TransactionRequest;
import com.example.springboot.accounting.model.entities.Account;
import com.example.springboot.accounting.model.entities.BankReccord;
import com.example.springboot.accounting.model.entities.BankStatement;
import com.example.springboot.accounting.model.entities.Transaction;
import com.example.springboot.accounting.repository.AccountRepository;
import com.example.springboot.accounting.service.BankReccordService;
import com.example.springboot.accounting.service.TransactionService;

@Controller
public class TransactionApiController {
	Map<String, String> monthConversion = new HashMap<>();

	private final TransactionService transactionService;
	private final BankReccordService bankReccordService;
	private final AccountRepository accountRepo;

	@Autowired
	public TransactionApiController(AccountRepository accountRepo, TransactionService transactionService,
			BankReccordService bankReccordService) {
		super();
		this.transactionService = transactionService;
		this.bankReccordService = bankReccordService;
		this.accountRepo = accountRepo;
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

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				// split by comma
				String[] transactionData = line.split(",");
				// transactionData[0], transactionData[1]... should be your csv fields, parse
				// and map it to your Transaction object.
				// For example, if your csv is like: SaleRevenue,100,2023-01-01
				// transactionData[0] will be "SaleRevenue"
				// transactionData[1] will be "100"
				// transactionData[2] will be "2023-01-01"

				// Assuming a method in your service to save the transaction
				// financeStatementService.saveTransaction(transaction);
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error occurred while processing the file.", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("File processed successfully.", HttpStatus.OK);
	}

	public Double convertDouble(String numberString) {
		numberString = numberString.replace(" ", "").replace(",", ".");
		return Double.parseDouble(numberString);
	}

	private Date getDate(String date_Str, String year) {
		String day="";
		String month="";
	    Pattern pattern_english = Pattern.compile("^(\\d+)([a-zA-Zàâéêèìôùûç]{3})$");
	    Pattern pattern_french = Pattern.compile("^(\\d+)([A-Za-z]{4})$");
	    if(date_Str == null)
	    	System.err.println("value is null");
        Matcher matcher_english = pattern_english.matcher(date_Str);
        Matcher matcher_french = pattern_french.matcher(date_Str);
        String monthAbr="";
        if(matcher_english.find()) 
        {
        	day = matcher_english.group(1);
        	month = matcher_english.group(2);
        	
        	if(monthConversion.containsValue(month)) 
        	{
        		monthAbr=month;
        	}
        	else 
        	{
        		monthAbr =monthConversion.get(month);
        	}
        	
        }
        else if(matcher_french.find())
        {
        	day = matcher_french.group(1);
        	month = matcher_french.group(2);
        	
        	if(monthConversion.containsValue(month)) 
        	{
        		monthAbr=month;
        	}
        	else 
        	{
        		monthAbr =monthConversion.get(month);
        	}
        }
        else 
        {
        	System.err.println();
        }
		
		String dateString = day + "/" + monthAbr + "/" + year;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
		try {
			Date date = dateFormat.parse(dateString);

			System.out.println(date); // Output: Sun Feb 26 00:00:00 UTC 2021
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
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

		Date date_ = getDate(date_str, year);
		LocalDate date = getLocalDateFromDate(date_);
		LocalDate startBoundary = date.minusMonths(1).withDayOfMonth(date.minusMonths(1).lengthOfMonth());
		LocalDate endBoundary = date.withDayOfMonth(date.lengthOfMonth());
		boundaries.put("start", startBoundary);
		boundaries.put("end", endBoundary);
		return boundaries;

	}

	/**
	 * 
	 * @param dateToConvert
	 * @return
	 */
	public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
		return java.sql.Date.valueOf(dateToConvert);
	}

	/**
	 * 
	 * @param date_
	 * @return
	 */
	private LocalDate getLocalDateFromDate(Date date_) {
		LocalDate date = date_.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return date;
	}

	public String formatLocalDate(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM_dd", Locale.ENGLISH);
		return date.format(formatter);
	}

	@PostMapping("/addCreditCardTransaction")
	public ResponseEntity<Object> addCreditCardTransaction(@RequestBody CreditCardActivity activity) {
		// process activity object
		if(activity.getDateOperation().isEmpty())
			return ResponseEntity.noContent().build();
		Transaction transaction = new Transaction();
		BankReccord br = new BankReccord();
		BankStatement bs = new BankStatement();
		bs.setAcc(activity.getAcc());
		bs.setAccountName(activity.getAccount_name());
		String operation_date_str = activity.getDateOperation();
		Map<String, LocalDate> b = getBoundaries(activity.getYear(), operation_date_str);
		bs.setFrom(formatLocalDate(b.get("start")));
		bs.setTo(formatLocalDate(b.get("end")));
		bs.setYear(Integer.parseInt(activity.getYear()));
		// bs.setSuc(transaction.getSuc());
		bankReccordService.save(bs);
		transaction.setAccount(activity.getAcc());
		Double amount = currencyToDouble(activity.getMontant());
		// Remove currency symbol and thousands separators
		transaction.setAmount(amount);
		Date date = getDate(activity.getDateActivite(), activity.getYear());
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
		String cleanedAmountString = currencyToAmount(transactionRequest.getAmount());

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
	public double currencyToDouble(String currency) {
	    String value = currency.replace(",", ".").replace("$", "").trim();
	    return Double.parseDouble(value);
	}
	private String currencyToAmount(String ammount) {
		String cleanedAmountString = ammount.replaceAll("[^0-9.-]", "");
		return cleanedAmountString;
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
		Date date = getDate(transaction.getDATE(), transaction.getYear());
		Double ammount = getAmmount(transaction);
		br.setSolde(convertDouble(transaction.SOLDE));
		br.setAcc(transaction.getAcc());
		br.setDescription(transaction.getDESCRIPTION());
		br.setSuc(transaction.getSuc());
		br.setDate(date);

		if (null != ammount) {
			t = new Transaction();
			t.setDescription(transaction.getDESCRIPTION());
			t.setDate(date);
			t.setAmount(ammount);
			t.setAccount(transaction.getAcc());
			Transaction saved = transactionService.save(t);
			br.setTransaction(saved);
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

}
