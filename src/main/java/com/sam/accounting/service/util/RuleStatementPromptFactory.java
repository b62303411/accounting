package com.sam.accounting.service.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sam.accounting.model.AiFileResult;
import com.sam.accounting.model.AssistantAnswer;
import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.repository.RuleRepository;
import com.sam.accounting.service.ruleengine.Rule;

@Service
public class RuleStatementPromptFactory {
	@Autowired
	public OpenAiRequestService service;

	public List<String> context = new ArrayList<>();

	@Autowired
	public AccountManager accountManager;

	@Autowired
	public RuleRepository repo;

	public RuleStatementPromptFactory() {

	}

	private class Context {
		String prettyString;
		Rule rule;
	}

	public AiFileResult submituery(String transaction, String extraContext) {
		String accountList = "'accounts'{[";

		List<Account> list = accountManager.getAccounts();

		for (Account account : list) {
			accountList += "{ 'accountNumber':" + "'" + account.getAccountNumber() + "'," + "  'accountName':" + "'"
					+ account.getName() + "'," + "  'accountType':" + "'" + account.getType() + "',"
					+ "  'naturalBalence':" + "'" + account.getType().getNaturalBalance() + "'" + "}]}";

		}

		String transactionSample = "Transaction Sample: 7053, GOOGLE*GSUITE_up2tech, 2023-04-01, 8.97, Credit";

		String keyInsights = "Key Insights & Context:\r\n" + "1 : Many transaction description will be in french.\r\n"
				+ "2 : This is a software consulting company \n"
				+ "3 : Most of my revenue should come from my consulting fee and should go in the corresponding revenue account.\r\n"
				+ "4 : Misceleneaus revenue should be something like bank fee refund and so on.. "
				+ "5 : Many transactions are automated payments from a checking account to a VISA account.\r\n" + "";

		String realAccounts = "Real accounts: 5235425,7053,9115997  all other accounts are finantial devices";

		String sampleOfTheResults = "Sample of a rule as json: {\r\n"
				+ "    \"ruleName\": \"Visa Payment from Checking\",\r\n" + "    \"vendor\": \"Visa TD\",\r\n"
				+ "    \"conditions\": [\r\n" + "        { \"account\": \"5235425\" },\r\n"
				+ "        { \"descriptionContains\": [\"PMT PREAUTOR VISA TD\", \"PAIEMENT VISA TD\"] }\r\n"
				+ "    ],\r\n" + "    \"classification\": {\r\n" + "        \"debit\": {\r\n"
				+ "            \"account\": \"5235425\",\r\n"
				+ "            \"accountName\": \"TD_EVERY_DAY_A_BUSINESS_PLAN\"\r\n" + "        },\r\n"
				+ "        \"credit\": {\r\n" + "            \"account\": \"E005\",\r\n"
				+ "            \"accountName\": \"Bank Fees\"\r\n" + "        }\r\n" + "    }\r\n" + "}";

		String wantedResults = "I want to generate JSON rules that will classify these transactions for my "
				+ "accounting ledger."
				+ "Make sure that the rule you create have a significant name as origin and destination";

		List<Rule> rules =new ArrayList<Rule>();// repo.findAllRules();

		String ruleList = "Rules:";

		for (Rule rule : rules) {
			ruleList += rule.getRuleName() + ",";
		}

		List<String> local_context = new ArrayList<String>();
		List<String> critique_context = new ArrayList<String>();
		local_context.add(accountList);
		local_context.add(sampleOfTheResults);
		local_context.add(realAccounts);

		local_context.add(keyInsights);
		local_context.add(ruleList);
		local_context.add(transactionSample);
		local_context.addAll(context);
		local_context.add("please create a rule for this transaction:" + transaction);
		local_context.add(wantedResults);

		critique_context.addAll(local_context);

		try {
			 NormalCorrectionSaveStrategy first = new NormalCorrectionSaveStrategy();
			AssistantAnswer result_str = service.runPrompt(local_context, first);
			Context c = getRule(result_str);
			// context.add("You provided this rule:"+prettyString+" make sure it make sense
			// given our current list of accounts and make sure you only asnwer with the
			// json value");
			critique_context.add("You provided this rule:" + c.prettyString + " "
					+ "Please validate and critique de coherence of this proposition."
					+ "Ask yourself what vendor or client is named in the comment of the transaction "
					+ "Then ask yourself what kind of service is that client likely to provide "
					+ "For instance you should know that google is a cloud provider so most likely Software as a service fee"
					+ "Mta cloutier longtin and northon and rose are some of my professional providers."
					+ "Knowing this you should be able to deduce what is the logical account we need to target."
					+ "Provide Insight alligning the origin of the account and the provided description.  "
					+ "Write your critique and proposed change , formatted using the following json format {critique:' here is my analisys of the given transaction'} proposedChange:{ruleName\\\": \\\"Visa Payment fr ... etc}");
			CorrectionSaveStrategy ss = new CorrectionSaveStrategy();
			AssistantAnswer result_critique = service.runPrompts(critique_context,ss);
			
			if(result_critique.answer!=null) 
			{
				context.add(result_critique.answer.toPrettyString());
				saveResult(result_critique);
			}
			
			// Context c2 = getRule(result_critique);
			// System.out.println(c2.prettyString);

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void saveResult(AssistantAnswer result_critique) {
		JsonNode test = result_critique.answer.get("proposedChange");
		JSONObject jsonObject = new JSONObject(test.toPrettyString());
		//Rule r2 = this.repo.createRule(jsonObject);
		//repo.add(r2);

		// Initialize the ObjectMapper
		ObjectMapper mapper = new ObjectMapper();

		// Load the existing JSON file
		JsonNode rootNode;
		try {
			String pathname = "E:\\workspace\\accounting\\src\\main\\resources\\Rules2.json";
			rootNode = mapper.readTree(new java.io.File(pathname));

			// Modify the JSON. For instance, let's add a new rule (you'd need a more
			// complex structure for real rules)
			((ArrayNode) rootNode.get("rules")).add(test);

			// Serialize the modified JSON object back to a string
			String updatedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

			// Write to a file

			mapper.writeValue(new File(pathname), rootNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Context getRule(AssistantAnswer result_str) {
		Context c = new Context();
		ObjectMapper objectMapper = new ObjectMapper();
		c.prettyString = result_str.answer.toPrettyString();
		JSONObject jsonObject = new JSONObject(c.prettyString);
		//c.rule = this.repo.createRule(jsonObject);
		return c;
	}
}
