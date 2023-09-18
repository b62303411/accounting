package com.example.springboot.accounting.service;

import java.sql.SQLSyntaxErrorException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.events.EndDocument;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.TransactionNature;
import com.example.springboot.accounting.model.TransactionType;
import com.example.springboot.accounting.model.dto.TransactionRequest;
import com.example.springboot.accounting.model.entities.Transaction;

@Service
public class DataParsingService {

	Map<String, String> monthConversion = new HashMap<>();

	public DataParsingService() {
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
	 * @param numberString
	 * @return
	 */
	public Double parseDouble(String numberString) {
		Double num = null;
		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE); // Using France locale where comma is a decimal
																		// separator
		numberString = numberString.replaceAll("\\s+", "");
		try {
			System.out.println("Parsed double: " + numberString);
			Number number = format.parse(numberString);
			num = number.doubleValue();
			System.out.println("Parsed double: " + num);
		} catch (ParseException e) {
			System.err.println("Failed to parse number: " + e.getMessage());
		}

		return num;
	}

	/**
	 * 
	 * @param here
	 * @return
	 */
	public Date parseDate(String[] here) {
		Date date = null;
		String day = here[0];
		String month = here[1];
		String year = here[2];
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH); // date format
		System.err.println();
		try {
			String date_str = day + " " + monthConversion.get(month) + " " + year;
			date = formatter.parse(date_str);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return date;
	}

	public Date parseDate(String day_str, String month, int year) {
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH); // date format
		try {
			String date_str = day_str + " " + month + " " + year;
			System.out.println(date_str);
			date = formatter.parse(date_str);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return date;
	}

	public Date getDate(String date_Str, String year) {
		String day = "";
		String month = "";
		Pattern pattern_english = Pattern.compile("^(\\d+)([a-zA-Zàâéêèìôùûç]{3})$");
		Pattern pattern_french = Pattern.compile("^(\\d+)([A-Za-z]{4})$");
		if (date_Str == null)
			System.err.println("value is null");
		Matcher matcher_english = pattern_english.matcher(date_Str);
		Matcher matcher_french = pattern_french.matcher(date_Str);
		String monthAbr = "";
		if (matcher_english.find()) {
			day = matcher_english.group(1);
			month = matcher_english.group(2);

			if (monthConversion.containsValue(month)) {
				monthAbr = month;
			} else {
				monthAbr = monthConversion.get(month);
			}

		} else if (matcher_french.find()) {
			day = matcher_french.group(1);
			month = matcher_french.group(2);

			if (monthConversion.containsValue(month)) {
				monthAbr = month;
			} else {
				monthAbr = monthConversion.get(month);
			}
		} else {
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

	public double currencyToDouble(String currency) {
		String value = currency.replace(",", ".").replace("$", "").trim();
		return Double.parseDouble(value);
	}

	public String currencyToAmount(String ammount) {
		String cleanedAmountString = ammount.replaceAll("[^0-9.-]", "");
		return cleanedAmountString;
	}

	public Map<String, LocalDate> getBoundaries(String start_date_str, String end_date_str, String operation_date_str) {
		Map<String, LocalDate> boundaries = new HashMap<>();
		LocalDate start_date = parseDateString(start_date_str);
		LocalDate end_date = parseDateString(end_date_str);
		boundaries.put("start", start_date);
		boundaries.put("end", end_date);
		return boundaries;
	}
	
	public Date getDate(String date_str, Map<String, LocalDate> b) {
		LocalDate start_date = b.get("start");
		LocalDate end_date = b.get("end");
		
		Pattern pattern = Pattern.compile("(\\d{1,2})([a-zA-Zàâéêèìôùûç]+)");
        Matcher matcher = pattern.matcher(date_str);
        if (matcher.find()) {
            int day = Integer.parseInt(matcher.group(1));
            String monthStr = matcher.group(2).toLowerCase();  // Taking the first 3 letters of the month
            
            Map<String, Integer> monthMap = getMonths();
            if(!monthMap.containsKey(monthStr))
    			System.err.println(monthStr);
    		int month = monthMap.get(monthStr);
    		int year = 0;
    		if(start_date.getMonthValue() < end_date.getMonthValue()) 
    		{
    			year = start_date.getYear();
    			LocalDate ld = LocalDate.of(year, month, day);
    			return getDateFromLocal(ld);
    		}
    		else 
    		{
    			if(month >=  start_date.getMonthValue())
    			{
    				year=start_date.getYear();
    				LocalDate ld = LocalDate.of(year, month, day);
    				return getDateFromLocal(ld);
    			}
    			else  
    			{
    				year=end_date.getYear();
    				LocalDate ld = LocalDate.of(year, month, day);
    				return getDateFromLocal(ld);
    			}
    		
    		}
        }
 	
		return null;
	}

	private Date getDateFromLocal(LocalDate ld) {
		return Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
	
	public LocalDate parseDateString(String date_str) {
	
		int day;
		int year;
		String monthStr;
		Pattern pattern = Pattern.compile("(\\d{1,2})([a-zA-Zûé]+)?(\\d{4})");
        Matcher matcher = pattern.matcher(date_str);
        if (matcher.find()) {
             day = Integer.parseInt(matcher.group(1));
             monthStr = matcher.group(2).toLowerCase(); // Taking the first 3 letters of the month
             year = Integer.parseInt(matcher.group(3));
         	 Map<String, Integer> monthMap = getMonths();
         	if(!monthMap.containsKey(monthStr))
    			System.err.println(monthStr);
    		int month = monthMap.get(monthStr);
    		return LocalDate.of(year, month, day);
        }	
		return null;
		
	}

	private Map<String, Integer> getMonths() {
		Map<String, Integer> monthMap = new HashMap<>();
		monthMap.put("jan", 1);
		monthMap.put("feb", 2);
		monthMap.put("fév", 2);		
		monthMap.put("mar", 3);
		monthMap.put("apr", 4);
		monthMap.put("avr", 4);
		monthMap.put("may", 5);
		monthMap.put("mai", 5);	
		monthMap.put("jun", 6);
		monthMap.put("juin", 6);
		monthMap.put("jul", 7);
		monthMap.put("juil", 7);
		monthMap.put("aug", 8);
		monthMap.put("aoû", 8);
		monthMap.put("sep", 9);
		monthMap.put("oct", 10);
		monthMap.put("nov", 11);
		monthMap.put("dec", 12);
		monthMap.put("déc", 12);
		return monthMap;
	}

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
	public LocalDate getLocalDateFromDate(Date date_) {
		LocalDate date = date_.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return date;
	}

	public String formatLocalDate(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM_dd", Locale.ENGLISH);
		return date.format(formatter);
	}

	public Double getAmmount(TransactionRequest transaction) {
		String depots = transaction.getDEPOTS();
		String retrait = transaction.getRETRAITS();
		if (null != depots && !depots.isBlank()) {
			return convertDouble(depots);
		} else if (null != retrait && !retrait.isBlank()) {
			return -convertDouble(retrait);
		}
		return null;
	}

	public Double convertDouble(String numberString) {
		numberString = numberString.replace(" ", "").replace(",", ".");
		return Double.parseDouble(numberString);
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

	

}
