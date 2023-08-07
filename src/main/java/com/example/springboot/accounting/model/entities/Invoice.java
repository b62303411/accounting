package com.example.springboot.accounting.model.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Invoice {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Double amount;
	
	private Double tps;
	
	private Double tvq;
	
	private String recipient;
	
	private String origine;
	
	private String description;
	
	private Date date;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Attachment> attachments;
	
	public Invoice()
	{
		attachments = new ArrayList<Attachment>();
	}
	


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}
	
	public void addAttachment(Attachment att) 
	{
		this.attachments.add(att);
	}



	public String getDescription() {
		return description;
	}



	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public List<Attachment> getAttachments() {
		return attachments;
	}



	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	
}
