package com.sam.accounting.service;

import java.util.Date;

import com.sam.accounting.model.IncomeStatementWhiteBoard;

public interface IRunner {

	void run(int fiscal_year, IncomeStatementWhiteBoard wb, Date endDate);

}
