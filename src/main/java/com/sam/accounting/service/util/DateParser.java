package com.sam.accounting.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class DateParser {
    //01 May 2021
	public Date parseDate(String date_str) {
		Date date = null;
		SimpleDateFormat formatter_cl = new SimpleDateFormat("yyyy-MM-dd");		
		SimpleDateFormat formatter_am = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		List<SimpleDateFormat> formats = new ArrayList();

		formats.add(formatter_am);
		formats.add(formatter_cl);
		for (SimpleDateFormat simpleDateFormat : formats) {
			try {
				date = simpleDateFormat.parse(date_str);
			} catch (ParseException e) {

			}
		}
		return date;
	}
}
