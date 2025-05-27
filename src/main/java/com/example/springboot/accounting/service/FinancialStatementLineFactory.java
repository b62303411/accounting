package com.example.springboot.accounting.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.dto.FinancialStatementLine;

@Service
public class FinancialStatementLineFactory {
	
	private HashMap<String,String> tooltipMap;
	public FinancialStatementLineFactory(){
	tooltipMap = new HashMap<>();
    tooltipMap.put("Revenue", "Revenue is the income that a business has from its normal business activities, usually from the sale of goods and services to customers.");
    tooltipMap.put("Cogs", "Cost of Goods Sold (COGS) is the direct costs attributable to the production of the goods sold by a company. This includes the cost of the materials used in creating the good along with the direct labor costs used to produce the good.");
    tooltipMap.put("Gross Profit", "Gross Profit is the profit a company makes after deducting the costs associated with making and selling its products, or the costs associated with providing its services.");
    tooltipMap.put("Operating Expenses", "Operating Expenses are the costs which a company incurs as part of its business operations, but not directly tied to production of goods or services. These include items such as rent, utilities, office supplies, and legal costs.");
    tooltipMap.put("Operating Income", "Operating Income, also known as operating profit or operating earnings, is the amount of profit realized from a business's operations, after deducting operating expenses such as wages, depreciation, and cost of goods sold.");
    tooltipMap.put("Other Expenses", "Other Expenses typically refer to costs that are not directly tied to a company’s core business operations. They are often related to financing or investments and could include costs like interest expense on debt, or loss from selling an asset.");
    tooltipMap.put("Other Revenue", "Other Revenue is income that comes from activities not directly related to the main business activity. Examples include income from interest, rent, or sales of assets.");
    tooltipMap.put("Net Income", "Net Income is a company's total earnings, or profit. It's calculated by taking revenues and subtracting the costs of doing business such as depreciation, interest, taxes, and other expenses. It's the bottom line of the income statement.");
	}
    /**
     * 
     * @param statement
     * @param value
     * @param title
     * @return
     */
	public FinancialStatementLine makeFs(Double value, String title) {
		return new FinancialStatementLine(title, value,tooltipMap.get(title));
	}
}
