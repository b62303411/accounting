package com.example.springboot.accounting.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Transient;

/**
 * In finance, the fiscal year is a one-year period that companies and
 * governments use for financial reporting and budgeting. The fiscal year is not
 * always the same as the calendar year; it might start and end at any point in
 * the year. The convention is to define a fiscal year according to the needs
 * and operational cycle of the specific organization.
 * 
 * For example:
 * 
 * In the United States, the federal government's fiscal year starts on October
 * 1 and ends on September 30.
 * 
 * In the United Kingdom, the government's fiscal year starts on April 1 and
 * ends on March 31.
 * 
 * In Australia, the government's fiscal year starts on July 1 and ends on June
 * 30.
 * 
 * Many companies, particularly multinational corporations, use a fiscal year
 * that corresponds to the calendar year, i.e., starting on January 1 and ending
 * on December 31.
 * 
 * Note that whatever the fiscal year used, it should remain consistent from
 * year to year for comparability of annual financial statements.
 * 
 * The start and end date of the fiscal year would be stated clearly in the
 * notes to the financial statements.
 * 
 * There's no universal convention that suits every company, as it depends on
 * the nature of the business, the cycle of its operations, and often the
 * geographical region it operates in
 */
public class FiscalYearEnd {
	public int day;
	public String month;
	@Transient
	Map<String, String> monthMap = new HashMap<>();

	public FiscalYearEnd(int day, String month) {
		this.day = day;
		this.month = month;
		// Map to convert 3-letter abbreviation to full month name

		monthMap.put("Jan", "JANUARY");
		monthMap.put("Feb", "FEBRUARY");
		monthMap.put("Mar", "MARCH");
		monthMap.put("Apr", "APRIL");
		monthMap.put("May", "MAY");
		monthMap.put("Jun", "JUNE");
		monthMap.put("Jul", "JULY");
		monthMap.put("Aug", "AUGUST");
		monthMap.put("Sep", "SEPTEMBER");
		monthMap.put("Oct", "OCTOBER");
		monthMap.put("Nov", "NOVEMBER");
		monthMap.put("Dec", "DECEMBER");
	}

	public Map<String, LocalDate> getFiscalYearBoundaries(int year) {
		Map<String, LocalDate> boundaries = new HashMap();
		Month fiscalMonth = Month.valueOf(month.toUpperCase());
		LocalDate startDate = LocalDate.of(year - 1, fiscalMonth, day).plusDays(1);
		LocalDate endDate = getLastDay(year);
		boundaries.put("start", startDate);
		boundaries.put("end", endDate);
		return boundaries;
	}

	private LocalDate getLastDay(int year) {
		Month fiscalMonth = Month.valueOf(month.toUpperCase());
		LocalDate endDate = LocalDate.of(year, fiscalMonth, day);
		return endDate;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getFiscalYear(Date date) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Month fiscalStartMonth;
		if (monthMap.containsKey(month)) {
			fiscalStartMonth = Month.valueOf(monthMap.get(month));
		} else {
			fiscalStartMonth = Month.valueOf(month);
		}

		LocalDate fiscalStart = LocalDate.of(localDate.getYear(), fiscalStartMonth, day);
		if (localDate.isBefore(fiscalStart)) {
			// If the date is before the start of the fiscal year, it belongs to the
			// previous fiscal year.
			return localDate.getYear() - 1;
		} else {
			return localDate.getYear();
		}

	}

	public Date getLastDayDate(int fy) {
		LocalDate lastDay = getLastDay(fy);

		LocalDate dayBefore = lastDay.minusDays(1);

		// Convert to java.util.Date
		Instant instant = dayBefore.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		Date utilDate = Date.from(instant);

		return utilDate;
	}

}
