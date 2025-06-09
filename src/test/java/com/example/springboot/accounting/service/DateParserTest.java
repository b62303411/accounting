package com.example.springboot.accounting.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.sam.accounting.service.util.DateParser;

class DateParserTest {

	@Test
	void test() {
		
		DateParser parser = new DateParser();
		
		
		String dateString = "01 May 2021";
		
		Date result = parser.parseDate(dateString);
		

		assertNotNull(result);
	}

}
