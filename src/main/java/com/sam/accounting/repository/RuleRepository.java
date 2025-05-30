package com.sam.accounting.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.entities.qb.Account;
import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.ClassificationRule;
import com.sam.accounting.model.entities.qb.LedgerData;
import com.sam.accounting.model.entities.qb.LedgerRuleFactory;

@Service
public class RuleRepository {

	public List<ClassificationRule> operatingExpensesRules;
	
	public List<ClassificationRule> assetPurchaceRules;

	public List<ClassificationRule> mem;
	
	LedgerData fixAccountInfo = new LedgerData("7053", "5235425", "9115997");
	
	@Autowired
	public AccountManager manager;
	
	@Autowired
	public LedgerRuleFactory factory;

	public RuleRepository() {
		mem = new ArrayList<ClassificationRule>();
		operatingExpensesRules = new ArrayList<ClassificationRule>();
		assetPurchaceRules = new ArrayList<ClassificationRule>();
	}

	public void createRules() {

		// rules = this.ruleRepo.findAllRules();

		addTrainingPurchaceRule(fixAccountInfo.creditCardAccountNo, List.of("SANS"), "SANS Institute");

		addOfficeEquipmentPurchaceRule(List.of("Amazon", "AMZNMktpCA"), "Amazon");
		addOfficeEquipmentPurchaceRule(List.of("BESTBUY"), "BESTBUY");
		addOfficeSuppliesRule(List.of("NEWEGG"), "NEWEGG");
		addOfficeSuppliesRule(List.of("Microsoft*Store"), "Microsoft");
		addOfficeSuppliesRule(List.of("Acme"), "Acme");
		addOfficeSuppliesRule(List.of("Dri Nvidia"), "Dri Nvidia");
		addOfficeSuppliesRule(List.of("Sears"), "Sears");
		addOfficeSuppliesRule(List.of("Best Buy"), "Best Buy");
		addOfficeSuppliesRule(List.of("Sparkfun"), "Sparkfun");
		addOfficeSuppliesRule(List.of("Master Vox Electronique"), "Master Vox");
		addOfficeSuppliesRule(List.of("AMZN Mktp ", "AMZNMktpCA", "amazon", "Amazon.ca", "Amazon*"),
				List.of("PrimeMemberamazon"), "Amazon");
		addOfficeSuppliesRule(List.of("MAGASINCDNTIRE"), "MAGASIN CDN TIRE");
		addOfficeSuppliesRule(List.of("THEHOMEDEPOT"), "THE HOME DEPOT");
		addOfficeSuppliesRule(List.of("FEDEX-YUDMONTREAL"), "FEDEX");
		addOfficeSuppliesRule(List.of("DOLLARAMA"), "DOLLARAMA");
		addOfficeSuppliesRule(List.of("BUREAUENGROS", "STAPLES"), "BUREAU EN GROS");
		addOfficeSuppliesRule(List.of("COOP", "COOP ETS MONTREAL"), "COOP ETS MONTREAL");

		addTravelAndMealsRule(List.of("Nettoyeur"), "Nettoyeur");
		addTravelAndMealsRule(List.of("Subway"), "Subway");
		addTravelAndMealsRule(List.of("Okane"), "Okane");
		addTravelAndMealsRule(List.of("Kanda"), "Kanda");
		addTravelAndMealsRule(List.of("Vinci"), "Vinci");
		addTravelAndMealsRule(List.of("Zibo", "RESTAURANT ZIBO"), "Zibo");
		addTravelAndMealsRule(List.of("Amir"), "Amir");
		addTravelAndMealsRule(List.of("Billet Ad Com"), "Billet Ad Com");
		addTravelAndMealsRule(List.of("Watan"), "Watan Boucherie");
		addTravelAndMealsRule(List.of("Rest Shaan Tandoori"), "Rest Shaan Tandoori");
		addTravelAndMealsRule(List.of("Hoang Huong"), "Hoang Huong");
		addTravelAndMealsRule(List.of("Thai Express"), "Thai Express");
		addTravelAndMealsRule(List.of("La Cuisine De"), "La Cuisine De");
		addTravelAndMealsRule(List.of("New York Grill"), "New York Grill");
		addTravelAndMealsRule(List.of("Basha"), "Basha");
		addTravelAndMealsRule(List.of("Piazza Pazza"), "Piazza Pazza");
		addTravelAndMealsRule(List.of("Watan Boucherie"), "Amigos Resto");
		addTravelAndMealsRule(List.of("Amigos Resto"), "Amigos Resto");
		addTravelAndMealsRule(List.of("Grillades Torino"), "Grillades Torino");
		addTravelAndMealsRule(List.of("Les Brasseurs Brossard"), "Les Brasseurs Brossard");
		addTravelAndMealsRule(List.of("PARKINGOTTAWA"), "PARKING OTTAWA");
		addTravelAndMealsRule(List.of("INDIGO"), "INDIGO");
		addTravelAndMealsRule(List.of("VINCIPARK", "VINCI PARK"), "VINCI PARK TOUR ALTITU MONTREAL");
		addTravelAndMealsRule(List.of("ECOLEDETECHNOLOGIQPSES"), "ECOLE DE TECHNOLOGI QPSES");
		addTravelAndMealsRule(List.of("RESTAURANTZIBOMONTREAL"), "RESTAURANT ZIBO MONTREAL");
		addTravelAndMealsRule(List.of("IVANHOECAMBRIDGEINC", "IVANHOE"), "IVANHOE CAMBRIDGE INC");
		addTravelAndMealsRule(List.of("LOT39MONTREAL"), "LOT 39 MONTREAL");
		addTravelAndMealsRule(List.of("OLDDUBLINPUBMONTREAL"), "OLD DUBLIN PUB MONTREAL");
		addTravelAndMealsRule(List.of("SERVICESDETRANSPORTADORVAL"), "SERVICES DE TRANSPORT A DORVAL");

		addSasRule(List.of("PrimeMemberamazon"), "Amazon.ca");
		addSasRule(List.of("ADOBESEND", "AdobeInc"), "Adobe Inc");
		addSasRule(List.of("GOOGLE"), "Google");

		addProfessionalFeeRule( List.of("Northon and rose"), "Northon and rose");
		addProfessionalFeeRule(
				List.of("Cloutier & Longtin", "Cloutier Longtin Accounting", "Cloutier Longtin"), "Cloutier & Longtin");
		addBankFeeRule( List.of("FRAIS MENS PLAN SERV", "FRAIS-COMMANDE CHEQ"), "TD");
	}

	private void addSasRule(List<String> of, String string) {
		addSasRule(of, string, fixAccountInfo.creditCardAccountNo);
		
	}

	private void addBankFeeRule(List<String> of, String string) {
		addBankFeeRule(fixAccountInfo.checkAccountNo, of, string);
		
	}

	private void addProfessionalFeeRule(List<String> of, String string) {
		addProfessionalFeeRule(fixAccountInfo.checkAccountNo,of, string);
		
	}



	private void addTravelAndMealsRule(List<String> of, String string) {
		addTravelAndMealsRule(fixAccountInfo.creditCardAccountNo, of, string);
	}

	
	private void addOfficeSuppliesRule(List<String> of, List<String> of2, String vendor) {
		addOfficeSuppliesRule(fixAccountInfo.creditCardAccountNo, of,of2, vendor);

	}
	
	private void addOfficeSuppliesRule(List<String> of, String string) {
		addOfficeSuppliesRule(fixAccountInfo.creditCardAccountNo, of, string);
	}

	private void addOfficeEquipmentPurchaceRule(List<String> list, String string) {
		addOfficeEquipementPurchaceRule(fixAccountInfo.creditCardAccountNo, list, string);
	}

	/**
	 * 
	 * @param accountNo
	 * @param keywords
	 * @param vendor
	 */
	private void addTrainingPurchaceRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeTrainingRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);

	}

	private void addBankFeeRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeBankFeeRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	/**
	 * 
	 * @param accountNo
	 * @param keywords
	 * @param vendor
	 */
	private void addTravelAndMealsRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeTravelAndMealRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	private void addOfficeEquipementPurchaceRule(String accountNo, List<String> keywords, String vendor) {
//		Account creditCard = getCreditCardAccount();
//		Account oe = getAccountByName("Office Equipment");
		ClassificationRule amp = factory.makeOfficeEquipementPurchaceRule(keywords, vendor, "Office Equipment",
				"VISA_TD_REMISES_AFFAIRES");
		this.assetPurchaceRules.add(amp);
	}

	private Account getAccountByName(String string) {
		return manager.getAccountByName(string);
	}

	private Account getCreditCardAccount() {
		return this.manager.getAccountByAccountNo(null);
	}

	/**
	 * 
	 * @param accountNo
	 * @param keywords
	 * @param vendor
	 */
	private void addProfessionalFeeRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeProfessionalFeesRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	/**
	 * 
	 * @param keywords
	 * @param vendor
	 * @param accountNo
	 */
	private void addSasRule(List<String> keywords, String vendor, String accountNo) {
		ClassificationRule amp = factory.makeSasRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	private void addOfficeSuppliesRule(String accountNo, List<String> keywords, String vendor) {
		ClassificationRule amp = factory.makeOfficeSuppliesRule(keywords, vendor, accountNo);
		operatingExpensesRules.add(amp);
	}

	private void addOfficeSuppliesRule(String accountNo, List<String> keywords, List<String> exception, String vendor) {
		ClassificationRule amp = factory.makeOfficeSuppliesRule(keywords, vendor, accountNo);
		for (String string : exception) {
			amp.addExcludeWord(string);
		}
		operatingExpensesRules.add(amp);
	}


	public void add(ClassificationRule r2) {
		mem.add(r2);

	}
}
