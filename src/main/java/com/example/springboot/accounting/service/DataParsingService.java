package com.example.springboot.accounting.service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class DataParsingService {

	Map<String, String> monthConversion = new HashMap<>();
	public DataParsingService() 
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

}
