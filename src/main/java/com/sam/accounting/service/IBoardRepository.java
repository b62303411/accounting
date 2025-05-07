package com.sam.accounting.service;

import com.sam.accounting.model.IncomeStatementWhiteBoard;
import com.sam.accounting.service.FiscalYearService.DateBoundaries;

public interface IBoardRepository {

	IncomeStatementWhiteBoard getBoard(int fiscal_year);

	DateBoundaries getBoundaries(int fiscal_year);

}
