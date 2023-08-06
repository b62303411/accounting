package com.example.springboot.accounting.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.springboot.accounting.model.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	/*
	 * @Query("SELECT new FinancialStatementLine(t.type, SUM(t.amount)) " +
	 * "FROM Transaction t " + "WHERE EXTRACT(YEAR FROM t.date) = :year " +
	 * "GROUP BY t.type") public List<FinancialStatementLine>
	 * sumTransactionsByTypeAndYear(@Param("year") Integer year);
	 * 
	 * @Query("SELECT new FinancialStatementLine(t.type, SUM(t.amount)) " +
	 * "FROM Transaction t " + "WHERE t.date >= :startDate AND t.date <= :endDate "
	 * + "GROUP BY t.type") public List<FinancialStatementLine>
	 * sumTransactionsByTypeAndFiscalYear(@Param("startDate") LocalDate startDate,
	 * 
	 * @Param("endDate") LocalDate endDate);
	 * 
	 * @Query("SELECT new FinancialStatementLine(t.type, SUM(t.amount)) " +
	 * "FROM Transaction t " + "WHERE t.date <= :date " + "GROUP BY t.type") public
	 * List<FinancialStatementLine> sumTransactionsByTypeAndDate(@Param("date") Date
	 * date);
	 * 
	 * @Query("SELECT new FinancialStatementLine(t.type, SUM(t.amount)) " +
	 * "FROM Transaction t " + "WHERE t.date <= :date " + "GROUP BY t.type") public
	 * List<FinancialStatementLine> sumTransactionsByTypeAndDate(@Param("date")
	 * LocalDate date);
	 * 
	 * @Query("SELECT new FinancialStatementLine(t.type, SUM(t.amount)) " +
	 * "FROM Transaction t " +
	 * "WHERE t.isCashFlow = true AND EXTRACT(YEAR FROM t.date) = :year " +
	 * "GROUP BY t.type") public List<FinancialStatementLine>
	 * sumCashFlowTransactionsByTypeAndYear(@Param("year") Integer year);
	 * 
	 * @Query("SELECT new FinancialStatementLine(t.type, SUM(t.amount)) " +
	 * "FROM Transaction t " +
	 * "WHERE t.isCashFlow = true AND t.date BETWEEN :startDate AND :endDate " +
	 * "GROUP BY t.type") public List<FinancialStatementLine>
	 * sumCashFlowTransactionsByTypeAndFiscalYear(@Param("startDate") Date
	 * startDate,
	 * 
	 * @Param("endDate") Date endDate);
	 */
	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type = 'SalesRevenue' AND EXTRACT(YEAR FROM t.date) = :year")
	public Double getSalesRevenueForYear(@Param("year") Integer year);

	@Query("SELECT t " + "FROM Transaction t "
			+ "WHERE t.type = 'SalesRevenue' AND t.date BETWEEN :startDate AND :endDate")
	public List<Transaction> getSalesTransactionsForFiscalYear(@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type = 'SalesRevenue' AND t.date BETWEEN :startDate AND :endDate")
	public Double getSalesRevenueForFiscalYear(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type = 'CostOfGoods' AND EXTRACT(YEAR FROM t.date) = :year")
	public Double getCOGSForYear(@Param("year") Integer year);

	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type = 'CostOfGoods' AND t.date BETWEEN :startDate AND :endDate")
	public Double getCOGSForFiscalYear(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type IN ('OperatingExpenses', 'Depreciation', 'AccruedExpenses') AND EXTRACT(YEAR FROM t.date) = :year")
	public Double getOperatingExpensesForYear(@Param("year") Integer year);

	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type IN ('OperatingExpenses', 'Depreciation', 'AccruedExpenses') AND t.date BETWEEN :startDate AND :endDate")
	public Double getOperatingExpensesForFiscalYear(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT t FROM Transaction t WHERE t.type IN ('OperatingExpenses', 'Depreciation', 'AccruedExpenses') AND t.date BETWEEN :startDate AND :endDate")
	public List<Transaction> getExpensesTransactionsForFiscalYear(@Param("startDate")Date startDate,@Param("endDate") Date endDate);

	@Query("SELECT t FROM Transaction t WHERE t.type IN ('UnrealizedGainsLosses', 'LoanProceeds') AND t.date BETWEEN :startDate AND :endDate")
	public List<Transaction> getOtherExpensesTransactionsBetween(@Param("startDate")Date startDate,@Param("endDate") Date endDate);
	
	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type IN ('UnrealizedGainsLosses', 'LoanProceeds') AND t.date BETWEEN :startDate AND :endDate")
	public Double getOtherExpensesBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	
	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type IN ('InvestmentIncome', 'UnrealizedGainsLosses', 'LoanProceeds', 'DeptRepayment', 'AssetSales', 'AssetPurchased') AND EXTRACT(YEAR FROM t.date) = :year")
	public Double getOtherIncomeExpensesForYear(@Param("year") Integer year);

	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type IN ('InvestmentIncome', 'LoanProceeds', 'AssetSales') AND EXTRACT(YEAR FROM t.date) = :year")
	public Double getOtherIncomesForYear(@Param("year") Integer year);

	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type IN ('InvestmentIncome', 'LoanProceeds', 'AssetSales') AND t.date BETWEEN :startDate AND :endDate")
	public Double getOtherIncomesForFiscalYear(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	// Other Expenses
	@Query("SELECT SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type IN ('DeptRepayment', 'AssetPurchased') AND EXTRACT(YEAR FROM t.date) = :year")
	public Double getOtherExpensesForYear(@Param("year") Integer year);

	
	@Query("SELECT t FROM Transaction t WHERE EXTRACT(YEAR FROM t.date) = :year")
	public List<Transaction> findAllTransactionsByYear(@Param("year") Integer year);

	public List<Transaction> findAllByDescription(String description);

}
