package com.example.springboot.accounting.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.model.dto.LedgerEntryDTO;
import com.example.springboot.accounting.model.entities.qb.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;

@RestController
public class ExcelExportService {
	@Autowired
	private GeneralLedgerService gls;
	@Autowired
	private FinancialStatementService financeStatementService;
	@Autowired
	private LedgerTransactionToDto dtoParser;
	@Autowired
	private AccountManager accountManager;

	public ExcelExportService() {
		System.out.println();
	}

	CellStyle dollarStyle;
	CellStyle dateStyle;
	CellStyle headerStyle;
	HashMap<String, CellStyle> map = new HashMap();

	@GetMapping("/download/excel/{fy}")
	public ResponseEntity<byte[]> downloadExcel(@PathVariable("fy") int fy) throws IOException 
	{
		List<LedgerEntryDTO> entries = gls.getLedgerDtos(fy);
		Workbook workbook = new XSSFWorkbook();
		Sheet generalLedgerSheet = workbook.createSheet("General Ledger "+fy);
		String fileName = "wb-FY"+fy+".xlsx";
		
		return generateExcel(fileName,entries, workbook, generalLedgerSheet);
	}
	
	@GetMapping("/download/excel")
	public ResponseEntity<byte[]> downloadExcel() throws IOException {
		
		List<LedgerEntryDTO> entries = gls.getLedgerDtos();
		Workbook workbook = new XSSFWorkbook();
		// ... (your code to populate the workbook here)
		// Create General Ledger sheet
		Sheet generalLedgerSheet = workbook.createSheet("General Ledger");
		
		
		return generateExcel("wb.xlsx",entries, workbook, generalLedgerSheet);

	}

	private ResponseEntity<byte[]> generateExcel(String fileName,List<LedgerEntryDTO> entries, Workbook workbook,
			Sheet generalLedgerSheet) throws IOException {
		dollarStyle = workbook.createCellStyle();
		DataFormat df = workbook.createDataFormat();
		dollarStyle.setDataFormat(df.getFormat("$#,#0.00"));
		dateStyle = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));

		headerStyle = workbook.createCellStyle();
		headerStyle.setBorderLeft(BorderStyle.DOUBLE); // double lines border
		headerStyle.setBorderTop(BorderStyle.DOUBLE); // single line border
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 15);
		font.setBold(true);
		headerStyle.setFont(font);


		// TODO: Fill data for general ledger
		String[] columns = createHeaderRow("General Ledger",generalLedgerSheet);
		// Data
		int rowNum = 2;
		fillData(entries, generalLedgerSheet, columns, rowNum);
		
		createPagesPerAccounts(workbook, entries, rowNum);
		
		createPagesPerAccountType(workbook, entries, rowNum);
		
		createExpensesPages(workbook,entries,rowNum);
		
		// Try to determine file's content type
		String contentType = null;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(bos.toByteArray());
	}

	private void createPagesPerAccountType(Workbook workbook, List<LedgerEntryDTO> entries, int rowNum) {
		String[] columns;
		for (AccountType type : AccountType.values()) {
			String sheetname = type.name();
			Sheet perTypeSheet = workbook.createSheet(sheetname);
			columns = createHeaderRow(sheetname,perTypeSheet);
			List<LedgerEntryDTO> perAccountType= new ArrayList();
	
			for (LedgerEntryDTO item : entries) {
				if(type.name().equals(item.getAccountType())) 
				{
					perAccountType.add(item);
				}
				
			}
			fillData(perAccountType, perTypeSheet, columns, rowNum);
		}
	}
	
	private void createExpensesPages(Workbook workbook, List<LedgerEntryDTO> entries, int rowNum) {
		String[] columns;
	
		List<Account> accounts = accountManager.getAccounts();
		List<LedgerEntryDTO> taxableExpenses= new ArrayList();
		String sheetname = "Taxable Expenses";
		Sheet perAccountSheet = workbook.createSheet(sheetname);
		columns = createHeaderRow(sheetname,perAccountSheet);
		
		for (Account account : accounts) {
			if(account.getAccountType()==AccountType.EXPENSE && account.isTaxable()) 
			{
				for (LedgerEntryDTO item : entries) {
					if(account.getAccountNumber().equals(item.getGlAccountNumber())) 
					{
						taxableExpenses.add(item);
					}		
				}
			}
		}
		fillData(taxableExpenses, perAccountSheet, columns, rowNum);
	}

	private void createPagesPerAccounts(Workbook workbook, List<LedgerEntryDTO> entries, int rowNum) {
		String[] columns;
		List<Account> accounts = accountManager.getAccounts();
	
		for (Account account : accounts) {
			String sheetname = account.getName();
			Sheet perAccountSheet = workbook.createSheet(sheetname);
			columns = createHeaderRow(sheetname,perAccountSheet);
			List<LedgerEntryDTO> perAccount= new ArrayList();
	
			for (LedgerEntryDTO item : entries) {
				if(account.getAccountNumber().equals(item.getGlAccountNumber())) 
				{
					perAccount.add(item);
				}
				
			}
			fillData(perAccount, perAccountSheet, columns, rowNum);
		}
	}

    // 0 Date	
	// 1 Account Type	
	// 2 GL Account Name	
	// 3 GL Account Number	
	// 4 Vendor/Client	
	// 5 Debit	
	// 6 Credit	
	// 7 Balance	
	// iBalance	
	// ABalance	
	// Message
	private void fillData(List<LedgerEntryDTO> entries, Sheet generalLedgerSheet, String[] columns, int rowNum) {
		for (LedgerEntryDTO entry : entries) {
			Row row = generalLedgerSheet.createRow(rowNum++);
			setDateValue(row, 0, entry.getDate());
			row.createCell(1).setCellValue(entry.getAccountType());
			row.createCell(2).setCellValue(entry.getGlAccountName());
			row.createCell(3).setCellValue(entry.getGlAccountNumber());
			row.createCell(4).setCellValue(entry.getVendorOrClient());
			if (entry.getDebit() != null) {
				setCurrencyValue(row, 5, entry.getDebit());
			}
			if (entry.getCredit() != null) {
				setCurrencyValue(row, 6, entry.getCredit());
			}
			
			Cell balanceCell = row.createCell(7);
		    String addressOfDebit = "F" + (rowNum); // current row's debit
		    String addressOfCredit = "G" + (rowNum); // current row's credit
		    
			if(rowNum == 3) {  // assuming 1 is the first data row after header, adjust if different
			    balanceCell.setCellFormula(addressOfDebit + " - " + addressOfCredit);
			}
			else {
			    String addressOfPreviousBalance = "H" + (rowNum-1); // previous row's balance
			    balanceCell.setCellFormula(addressOfPreviousBalance + " + " + addressOfDebit + " - " + addressOfCredit);
			}

			
			setCurrencyValue(row, 8, entry.getBalence());

			if (entry.getAbalence() != null)
				setCurrencyValue(row, 9, entry.getAbalence());
			
	

			row.createCell(10).setCellValue(entry.getMessage());
		}
		for (int i = 0; i < columns.length; i++) {
			generalLedgerSheet.autoSizeColumn(i);
		}
		
		
		generalLedgerSheet.setAutoFilter(new CellRangeAddress(1, generalLedgerSheet.getLastRowNum(), 1, 3));
	}

	private void setDateValue(Row row, int column, Date value) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(dateStyle);
	}

	private void setCurrencyValue(Row row, int column, Double value) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(dollarStyle);
	}

	private String[] createHeaderRow(String title, Sheet generalLedgerSheet) {
		Row titleRow = generalLedgerSheet.createRow(0);
		generalLedgerSheet.addMergedRegion(new CellRangeAddress(0,0,1,3));
		Cell cell_title = titleRow.createCell(1);
		cell_title.setCellStyle(headerStyle);
		cell_title.setCellValue(title);
		Row headerRow = generalLedgerSheet.createRow(1);
		String[] columns = { "Date", "Account Type", "GL Account Name", "GL Account Number", "Vendor/Client", "Debit",
				"Credit", "Balance", "iBalance","ABalance", "Message" };

		map.put("Debit", dollarStyle);
		map.put("Credit", dollarStyle);
		map.put("Balance", dollarStyle);
		map.put("ABalance", dollarStyle);
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellStyle(headerStyle);
//        	switch(i) 
//        	{
//        	case 5:
//        	case 6:
//        	case 7:
//        	case 8:
//        		cell.setCellStyle(dollarStyle);
//        	}

			cell.setCellValue(columns[i]);
		}
		return columns;
	}
	
	
	
//	// Create a sheet per year
//	for (int year = 2015; year <= 2023; year++) {
//		String sheetname = "Year " + year;
//		Sheet yearSheet = workbook.createSheet(sheetname);
//		IncomeStatementDto dto = this.financeStatementService.incomeStatementService.generateIncomeStatement(year);
//		List<LedgerEntryDTO> dtos = dtoParser.convertToLedgerEntryDTOs(dto.wb.getTransactions());
//		String[] cmss = createHeaderRow(sheetname,yearSheet);
//		fillData(dtos, yearSheet, cmss, rowNum);
//		
//	}
	
	

//	// Create a sheet per account
//	Sheet accountSheet = workbook.createSheet("Accounts");
//	// TODO: Fill data for accounts
//	Set<String> sheets  = new HashSet<String>();
//	List<Account> accounts = accountManager.getAccounts();
//	for (int year = 2016; year <= 2023; year++) {
//
//		IncomeStatementDto dto = this.financeStatementService.incomeStatementService.generateIncomeStatement(year);
//		
//		List<LedgerEntryDTO> dtos = dtoParser.convertToLedgerEntryDTOs(dto.wb.getTransactions());
//		
//		AccountType[] values = AccountType.values();
//		for (AccountType accountType : values) {
//			String sheetname = accountType + " FY_" + year;
//			Sheet accountYearSheet = workbook.createSheet(sheetname);
//			String[] cmss = createHeaderRow(sheetname,accountYearSheet);
//			List<LedgerEntryDTO> perAccount= new ArrayList();
//			for (LedgerEntryDTO item : dtos) {
//			    AccountType type = accountManager.getAccountByAccountNo(item.getGlAccountNumber()).getAccountType();
//				if(type.equals(accountType)) 
//				{
//					perAccount.add(item);
//				}
//				
//			}
//			fillData(perAccount, accountYearSheet, cmss, rowNum);
//			
//			
//		}
//
//	}
}
