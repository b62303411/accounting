package com.sam.accounting.model.dto;

/**
 * 
 */
public class FinancialStatementLine {
	private String description;
    private Double amount;
    private String tooltip;
    
    /**
     * 
     * @param description
     * @param amount
     * @param tooltip 
     */
	public FinancialStatementLine(String description, Double amount, String tooltip) {
		super();
		this.description = description;
		this.amount = amount;
		this.tooltip=tooltip;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
    
    
}
