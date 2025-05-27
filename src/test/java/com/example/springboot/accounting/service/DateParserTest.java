package com.example.springboot.accounting.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

<<<<<<< HEAD
import com.sam.accounting.service.util.DateParser;
=======
import com.example.springboot.accounting.service.util.DateParser;
>>>>>>> refs/remotes/origin/main

class DateParserTest {

	@Test
	void test() {
		
		DateParser parser = new DateParser();
		
		
		String dateString = "01 May 2021";
		
		Date result = parser.parseDate(dateString);
		

		assertNotNull(result);
	}

}
