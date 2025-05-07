package com.example.springboot.accounting.service;

import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.sam.accounting.service.CompanyProfileService;
import com.sam.accounting.service.FiscalYearService;

class FiscalYearServiceTest {

	@Test
	void test() {
		FiscalYearService service = new FiscalYearService();
		CompanyProfileService cps = new CompanyProfileService();
		service.setProfile(cps);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date testDate = sdf.parse("2018-03-25");
			int expectedFiscalYear = 2018; // Assuming fiscal year from April 1 to March 31
			int actualFiscalYear = service.getFiscalYear(testDate);
			assertEquals(expectedFiscalYear, actualFiscalYear);

			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Test
	void test2() {
		FiscalYearService service = new FiscalYearService();
		CompanyProfileService cps = new CompanyProfileService();
		service.setProfile(cps);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date testDate = sdf.parse("2018-01-02");
			int expectedFiscalYear = 2018; // Assuming fiscal year from April 1 to March 31
			int actualFiscalYear = service.getFiscalYear(testDate);
			assertEquals(expectedFiscalYear, actualFiscalYear);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	@Test
	void test3() {
		FiscalYearService service = new FiscalYearService();
		CompanyProfileService cps = new CompanyProfileService();
		service.setProfile(cps);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date testDate = sdf.parse("2018-03-31");
			//LocalDate fiscalYearStart = LocalDate.parse("2018-03-31");
			int expectedFiscalYear = 2018; // Assuming fiscal year from April 1 to March 31
			int actualFiscalYear = service.getFiscalYear(testDate);
			assertEquals(expectedFiscalYear, actualFiscalYear);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
