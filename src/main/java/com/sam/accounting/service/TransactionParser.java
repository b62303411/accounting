package com.sam.accounting.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sam.accounting.model.AiFileResult;
import com.sam.accounting.model.TransactionNature;
import com.sam.accounting.model.entities.Transaction;
import com.sam.accounting.service.util.BankStatementPromptFactory;
import com.sam.accounting.service.util.PdfService;

@Service
public class TransactionParser {
	
	@Autowired
	private PdfService pdfService;
	
	@Autowired
	private DataParsingService dataParsingService;
	
	@Autowired
	private BankStatementPromptFactory pspf;
	
	@Autowired
	private TransactionService transactionService;
	
	Map<String, String> monthConversion = new HashMap<>();
	
	public TransactionParser() 
	{
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
	public AiFileResult populateTransaction(MultipartFile file, String extraContext)
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

}
