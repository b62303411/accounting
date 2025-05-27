package com.example.springboot.accounting.service.ledger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.qb.ClassificationRule;
import com.example.springboot.accounting.model.entities.qb.LedgerRuleFactory;

@Service
public class ReadRuleFromJson {
	@Autowired
	private LedgerRuleFactory factory;
	
	
	public List<ClassificationRule> findAllRules() {
		String rule = loadRulesFromResource();
		return translateJsonToRules(rule);
	}
	
	public List<ClassificationRule> translateJsonToRules(String json) {
		List<ClassificationRule> rulesList = new ArrayList<>();

		JSONObject rulesObj = new JSONObject(json);
		JSONArray rules = rulesObj.getJSONArray("rules");
		for (int j = 0; j < rules.length(); j++) {
			JSONObject rule = rules.getJSONObject(j);
			List<ClassificationRule> rules_l = createRules(rule);
			rulesList.addAll(rules_l);
		}

		return rulesList;
	}

	public List<ClassificationRule> createRules(JSONObject rulesObj) {

		List<ClassificationRule> rules = new ArrayList();

		String credited = rulesObj.getString("credited");
		String debited = rulesObj.getString("debited");

		JSONArray cases = rulesObj.getJSONArray("cases");

		for (int j = 0; j < cases.length(); j++) {
			JSONObject mathc_case = cases.getJSONObject(j);

			String vendor = mathc_case.getString("vendor");
			JSONArray conditions = mathc_case.getJSONArray("descriptionContains");
			List<String> keywords = ((JSONArray) conditions).toList().stream().map(Object::toString)
					.collect(Collectors.toList());
			ClassificationRule rule = factory.makeExpence(keywords, vendor, credited, debited);
			rules.add(rule);

		}

		return rules;

	}
	
	private String loadRulesFromResource() {
		InputStream rulesStream = getClass().getClassLoader().getResourceAsStream("Rules.json");
		if (rulesStream == null) {
			throw new RuntimeException("Failed to find the rules resource.");
		}
		byte[] bytes;
		try {
			bytes = rulesStream.readAllBytes();
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load rules from resource.", e);
		}
	}
	
//	public Rule createRule(JSONObject ruleJson) {
//	Rule rule = new Rule();
//	rule.setRuleName(ruleJson.getString("ruleName"));
//
//	// Process conditions
//	JSONArray conditionsJson = ruleJson.getJSONArray("conditions");
//	List<Condition> conditions = new ArrayList<>();
//	for (int j = 0; j < conditionsJson.length(); j++) {
//		JSONObject conditionJson = conditionsJson.getJSONObject(j);
//		Condition condition = new Condition();
//
//		if (conditionJson.has("account")) {
//			condition.setAccount(conditionJson.getString("account"));
//		}
//		if (conditionJson.has("descriptionContains")) {
//			Object descriptionObj = conditionJson.get("descriptionContains");
//			if (descriptionObj instanceof JSONArray) {
//				condition.setDescriptionContains(((JSONArray) descriptionObj).toList().stream()
//						.map(Object::toString).collect(Collectors.toList()));
//			} else if (descriptionObj instanceof String) {
//				List<String> singleKeywordList = new ArrayList<>();
//				singleKeywordList.add((String) descriptionObj);
//				condition.setDescriptionContains(singleKeywordList);
//			}
//		}
//
//		conditions.add(condition);
//	}
//	rule.setConditions(conditions);
//
//	// Process classification
//	JSONObject classificationJson = ruleJson.getJSONObject("classification");
//	Classification classification = new Classification();
//
//	AccountAction debit = new AccountAction();
//	JSONObject debitJson = classificationJson.getJSONObject("debit");
//	debit.setAccount(debitJson.getString("account"));
//	debit.setAccountName(debitJson.getString("accountName"));
//	classification.setDebit(debit);
//
//	AccountAction credit = new AccountAction();
//	JSONObject creditJson = classificationJson.getJSONObject("credit");
//	credit.setAccount(creditJson.getString("account"));
//	credit.setAccountName(creditJson.getString("accountName"));
//	classification.setCredit(credit);
//
//	rule.setClassification(classification);
//	return rule;
//}

}
