package com.example.springboot.accounting.model;

import java.util.Arrays;
import java.util.List;

import com.example.springboot.accounting.model.dto.MenuOption;

public class MenuOptions {
	public static List<MenuOption> getOptions()
	{
		List<MenuOption> options =Arrays.asList(
			    new MenuOption("Transaction", "view/transactions/" ),
			    new MenuOption("Income Statement", "view/incomeStatement/" ),
			    new MenuOption("Balance Sheet", "view/balance/"),
			    new MenuOption("Revenues", "view/revenues/"),
			    new MenuOption("Expenses", "view/expenses/"),
			    new MenuOption("Bills", "view/bills/")
			);

		return options;
		
	}
}
