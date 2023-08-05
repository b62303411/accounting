package com.example.springboot.accounting.model.dto;

public class ReportAvailable {
		public String account;
		public String month;
		public int monthOrdinal;
		public boolean available;
		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public int getMonthOrdinal() {
			return monthOrdinal;
		}
		public void setMonthOrdinal(int monthOrdinal) {
			this.monthOrdinal = monthOrdinal;
		}
		public boolean isAvailable() {
			return available;
		}
		public void setAvailable(boolean available) {
			this.available = available;
		}
		
		
}
