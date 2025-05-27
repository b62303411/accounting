package com.example.springboot.accounting.service;

import java.util.Date;

import com.example.springboot.accounting.model.IncomeStatementWhiteBoard;

public class FiscalStrategyRunner {

	IRunner runner;
	IBoardRepository wbBoards;
	public FiscalStrategyRunner(IRunner run, IBoardRepository wbBoards)
	{
		this.wbBoards = wbBoards;
		this.runner=run;
	}
	
	public void run() {
		for (int fiscal_year = 2014; fiscal_year < 2025; fiscal_year++) {
			IncomeStatementWhiteBoard wb = wbBoards.getBoard(fiscal_year);
			Date endDate = wbBoards.getBoundaries(fiscal_year).date_end;
//			if (wb.beforeTaxIncome > 0) {

				runner.run(fiscal_year, wb, endDate);
//			}
		}
	}
}
