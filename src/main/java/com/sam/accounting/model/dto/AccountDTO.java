package com.sam.accounting.model.dto;

public class AccountDTO {

    private Long id;
    private String type;  // CHECKING, SAVINGS, etc.
    private String accountNo;
    private String accountName; // "TD EVERY DAY A BUSINESS PLAN", "VISA CARD"
    private String alias;
    private Double balance;
    private String accountingType; // Use String to avoid tight coupling with Enum

    // Default constructor
    public AccountDTO() {}

    // Constructor to initialize all fields
    public AccountDTO(Long id, String type, String accountNo, String accountName, String alias, Double balance, String accountingType) {
        this.id = id;
        this.type = type;
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.alias = alias;
        this.balance = balance;
        this.accountingType = accountingType;
    }

    // Getters and Setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getAccountingType() {
        return accountingType;
    }

    public void setAccountingType(String accountingType) {
        this.accountingType = accountingType;
    }
}
