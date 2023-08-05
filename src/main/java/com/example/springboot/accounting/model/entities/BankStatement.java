package com.example.springboot.accounting.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
@Entity
@Table(name = "BankStatement",
uniqueConstraints = {@UniqueConstraint(columnNames = {"year", "from_dm","to_dm","acc"})}
)
public class BankStatement {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
    private String acc;
    private String suc;
    private String accountName;
    private int year;
    private String from_dm;
    private String to_dm;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getSuc() {
		return suc;
	}
	public void setSuc(String suc) {
		this.suc = suc;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getFrom() {
		return from_dm;
	}
	public void setFrom(String from) {
		this.from_dm = from;
	}
	public String getTo() {
		return to_dm;
	}
	public void setTo(String to) {
		this.to_dm = to;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getFrom_dm() {
		return from_dm;
	}
	public void setFrom_dm(String from_dm) {
		this.from_dm = from_dm;
	}
	public String getTo_dm() {
		return to_dm;
	}
	public void setTo_dm(String to_dm) {
		this.to_dm = to_dm;
	}
    
    

}
