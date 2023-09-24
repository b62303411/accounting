package com.example.springboot.accounting.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.model.FinancialData;
import com.example.springboot.accounting.model.entities.Account;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@RestController
public class LatexGenerator {

	@Autowired
	AccountManager accountManager;
	Configuration cfg;

	public LatexGenerator() {
		cfg = getFreeMarkerConfiguration();
		System.err.println();
	}

	
	public freemarker.template.Configuration getFreeMarkerConfiguration() {
		freemarker.template.Configuration config = new freemarker.template.Configuration(
				freemarker.template.Configuration.VERSION_2_3_31);
		config.setClassForTemplateLoading(this.getClass(), "/templates/freemarker");
		return config;
	}

	public static String generateLaTeX(FinancialData data) {
		StringBuilder latex = new StringBuilder();

		latex.append("\\documentclass[12pt, letterpaper]{article}\n");
		latex.append("\\usepackage[utf8]{inputenc}\n");
		latex.append("\\usepackage{booktabs}\n");
		latex.append("\\usepackage{siunitx}\n");
		latex.append("\\title{État Financier}\n");
		latex.append("\\date{\\today}\n");
		latex.append("\\author{Nom de votre entreprise}\n");
		latex.append("\\begin{document}\n");
		latex.append("\\maketitle\n\n");

		latex.append("\\section*{Bilan au \\today}\n\n");
		latex.append(generateTable(data.balanceSheetAssets, "Actifs"));
		latex.append(generateTable(data.balanceSheetLiabilitiesAndEquity, "Passifs & Capitaux Propres"));

		latex.append("\\section*{Compte de résultat pour l'année terminée le \\today}\n\n");
		latex.append(generateTable(data.incomeStatementData, "Compte de Résultat"));

		latex.append("\\end{document}");

		return latex.toString();
	}

//	private String generateSection(String sectionTitle, List<AccountData> accounts) {
//		StringBuilder sectionBuilder = new StringBuilder("\\section*{" + sectionTitle + "}\n");
//		sectionBuilder.append("\\begin{tabular}{l S[table-format=10.2]}\n\\toprule\n");
//		sectionBuilder.append("Description & {Amount (\\$)} \\\\\n\\midrule\n");
//
//		for (AccountData account : accounts) {
//			sectionBuilder.append(account.getName() + " & " + account.getBalance() + " \\\\\n");
//		}
//
//		sectionBuilder.append("\\bottomrule\n\\end{tabular}\n");
//
//		return sectionBuilder.toString();
//	}

	private static String generateTable(Map<String, Double> data, String sectionName) {
		StringBuilder table = new StringBuilder();

		table.append("\\begin{tabular}{\n");
		table.append("  l\n");
		table.append("  S[table-format=6.2]\n");
		table.append("}\n");
		table.append("\\toprule\n");
		table.append("\\textbf{").append(sectionName).append("} & \\textbf{Montant (\\$)} \\\\\n");
		table.append("\\midrule\n");

		for (Map.Entry<String, Double> entry : data.entrySet()) {
			table.append(entry.getKey()).append(" & ").append(String.format("%.2f", entry.getValue()))
					.append(" \\\\\n");
		}

		table.append("\\bottomrule\n");
		table.append("\\end{tabular}\n\n");
		table.append("\\vspace{1cm}\n");

		return table.toString();
	}

	public static void main(String[] args) {
		FinancialData data = new FinancialData();
		data.populateSampleData();
		System.out.println(generateLaTeX(data));
	}

	private static String getPreamble() {
		return "\\documentclass[12pt, letterpaper]{article}\n" + "\\usepackage[utf8]{inputenc}\n"
				+ "\\usepackage{booktabs}\n" + "\\usepackage{siunitx}\n" + "\\title{État Financier}\n"
				+ "\\date{\\today}\n" + "\\author{Nom de votre entreprise}\n\n" + "\\begin{document}\n\n"
				+ "\\maketitle\n\n";
	}

	private static String getTableCode(Map<String, Double> data) {
		StringBuilder tableCode = new StringBuilder();
		tableCode.append("\\begin{tabular}{\n  l\n  S[table-format=6.2]\n  S[table-format=6.2]\n}\n");
		tableCode.append("\\toprule\n");
		tableCode.append("\\textbf{Éléments} & \\textbf{Débit (\\$)} & \\textbf{Crédit (\\$)} \\\\ \n\\midrule\n");

		data.forEach((key, value) -> {
			tableCode.append(key + " & ");
			if (value >= 0) {
				tableCode.append(value + " & \\\\ \n");
			} else {
				tableCode.append(" & " + (-value) + " \\\\ \n");
			}
		});

		tableCode.append("\\bottomrule\n\\end{tabular}\n\n\\vspace{1cm}\n\n");

		return tableCode.toString();
	}

	@GetMapping("/latex")
	public String getLatex() {
		FinancialData data = new FinancialData();
		data.populateSampleData();
		Map<String, Object> dataModel = new HashMap<>();

        List<Account> revenueAccounts = accountManager.getAccountByType(AccountType.REVENUE);
        List<Account> operatingExpenseAccounts= accountManager.getAccountByType(AccountType.EXPENSE, true);
        List<Account> otherExpenseAccounts= accountManager.getAccountByType(AccountType.EXPENSE, false);
        // Put values into root.
        dataModel.put("year", "2023");
        dataModel.put("companyName", "XYZ Inc.");
        dataModel.put("revenueAccounts", revenueAccounts);
        dataModel.put("operatingExpenseAccounts",operatingExpenseAccounts);
        dataModel.put("otherExpenseAccounts", otherExpenseAccounts);
        dataModel.put("incomeBeforeTax", 70000.0);
        dataModel.put("incomeTax", 14000.0);
        dataModel.put("incomeAfterTax", 56000.0);
		StringWriter stringWriter = new StringWriter();
		try {
			Template template = cfg.getTemplate("template.ftlh");
			template.process(dataModel, stringWriter);
		} catch (TemplateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringWriter.toString();

	
	}
}
