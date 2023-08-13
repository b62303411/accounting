package com.example.springboot.accounting.model.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Expense {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@OneToOne
	private Transaction transaction;
	@OneToOne
	private Invoice invoice;

	private Double totalBeforeSalesTaxes;

	private Double tps;

	private Double tvq;

	private String description;

	private Date date;

	public Double getTotalBeforeSalesTaxes() {
		if(null == totalBeforeSalesTaxes)
			return 0.0;
		return totalBeforeSalesTaxes;
	}

	public void setTotalBeforeSalesTaxes(double totalBeforeSalesTaxes) {
		this.totalBeforeSalesTaxes = totalBeforeSalesTaxes;
	}

	public Double getTps() {
		return tps;
	}

	public void setTps(Double tps) {
		this.tps = tps;
	}

	public Double getTvq() {
		return tvq;
	}

	public void setTvq(Double tvq) {
		this.tvq = tvq;
	}

	public double getTotal() {
		double value = 0.0;
		if (totalBeforeSalesTaxes != null) {
			value += totalBeforeSalesTaxes;
			if (null != tps)
				value += tps;
			if (null != tvq)
				value += tvq;
		}

		return value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public abstract String getTypeStr();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getSalesTaxes() {
		double value = 0;

		if (null != tps && null != tvq)
			value = tps + tvq;
		return value;
	}

}
