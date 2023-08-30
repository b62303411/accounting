package com.example.springboot.accounting.model;

import java.util.Date;
import java.util.Objects;



public class ExpenseKey { private double transactionAmount;
private Date date;
private String payee;
public ExpenseKey(double transactionAmount, Date transactionDate, String payee) {
    this.transactionAmount = transactionAmount;
    this.date = transactionDate;
    this.payee = payee;
}
// Make sure to implement equals() and hashCode() methods so this class works correctly in a HashMap
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExpenseKey that = (ExpenseKey) o;
    return Double.compare(that.transactionAmount, transactionAmount) == 0 && Objects.equals(date, that.date) && Objects.equals(payee, that.payee);
}

@Override
public int hashCode() {
    return Objects.hash(transactionAmount, date, payee);
}
public double getTransactionAmount() {
	return transactionAmount;
}
public void setTransactionAmount(double transactionAmount) {
	this.transactionAmount = transactionAmount;
}

public Date getDate() {
	return date;
}
public void setDate(Date date) {
	this.date = date;
}
public String getPayee() {
	return payee;
}
public void setPayee(String payee) {
	this.payee = payee;
}



}
