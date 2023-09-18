package com.example.springboot.accounting.service;

import java.util.Date;

import com.example.springboot.accounting.model.IncomeStatementWhiteBoard;

public interface IRunner {

	void run(int fiscal_year, IncomeStatementWhiteBoard wb, Date endDate);

}
