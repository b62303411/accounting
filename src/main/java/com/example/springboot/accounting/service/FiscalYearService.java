package com.example.springboot.accounting.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FiscalYearService {
	
	public class DateBoundaries {
		public Date date_start;
		public Date date_end;
	}
	
	public class LocalDateBoundaries {
		public LocalDate date_start;
		public LocalDate date_end;
	}
	
	@Autowired
	private CompanyProfileService profile;
	
	public int getFiscalYear(Date start) {

		System.out.println("Today's date: " + start);
		Calendar calendar = Calendar.getInstance();
		// Create a Calendar instance and set it to the current date

		calendar.setTime(start);
		// Add one day to the current date
		calendar.add(Calendar.DATE, 1);
		Date tomorrow = calendar.getTime();

		return profile.getProfile().getFiscalYearEnd().getFiscalYear(tomorrow);
	}
	
	public LocalDateBoundaries getLocalDateBoundaries(Integer year) 
	{
		LocalDateBoundaries b = new LocalDateBoundaries();
		Map<String, LocalDate> boudnaries = profile.getProfile().getFiscalYearEnd().getFiscalYearBoundaries(year);
		LocalDate start = boudnaries.get("start");
		LocalDate end = boudnaries.get("end");
		b.date_start=start;
		b.date_end=end;
		return b;
	}
	
	public DateBoundaries getBoundaries(Integer year) {
		LocalDateBoundaries lb = getLocalDateBoundaries(year);
		DateBoundaries b = new DateBoundaries();
		b.date_start = Date.from(lb.date_start.atStartOfDay(ZoneId.systemDefault()).toInstant());
		b.date_end = Date.from(lb.date_end.atStartOfDay(ZoneId.systemDefault()).toInstant());

		return b;
	}

	protected CompanyProfileService getProfile() {
		return profile;
	}

	protected void setProfile(CompanyProfileService profile) {
		this.profile = profile;
	}
	
	
}
