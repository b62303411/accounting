package com.example.springboot.accounting.service;

import com.example.springboot.accounting.model.IncomeStatementWhiteBoard;
import com.example.springboot.accounting.service.FiscalYearService.DateBoundaries;

public interface IBoardRepository {

	IncomeStatementWhiteBoard getBoard(int fiscal_year);

	DateBoundaries getBoundaries(int fiscal_year);

}
