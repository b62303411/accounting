package com.example.springboot.accounting.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.dto.LedgerEntryDTO;
import com.example.springboot.accounting.model.entities.Account;
import com.example.springboot.accounting.model.entities.Amortisation;
import com.example.springboot.accounting.model.entities.AmortisationLeg;
import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.model.entities.CompanyProfile;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;
import com.example.springboot.accounting.model.entities.qb.EntryType;
import com.example.springboot.accounting.model.entities.qb.Ledger;
import com.example.springboot.accounting.model.entities.qb.LedgerRuleFactory;
import com.example.springboot.accounting.model.entities.qb.Transaction;
import com.example.springboot.accounting.model.entities.qb.TransactionEntry;
import com.example.springboot.accounting.repository.AccountRepository;
import com.example.springboot.accounting.repository.AssetRepository;
import com.example.springboot.accounting.repository.TransactionRepository;

@Service
public class GeneralLedgerService {

	@Autowired
	public TransactionRepository t_repo;
	@Autowired
	public AccountRepository a_repo;
	
	@Autowired
	public AccountFactory accountFactory;
	
	@Autowired
	public LedgerRuleFactory ruleFactory;

	@Autowired
	public AccountManager accountManager;
	
	@Autowired
	public AssetRepository assetRepository;
	
	@Autowired
	public CompanyProfileService profileService;
	
	public Ledger l;
	
	public GeneralLedgerService()
	{
		
	}
	
	public List<LedgerEntryDTO> getLedgerDtos()
	{
		createAccounts(accountManager);
		l = new Ledger(accountManager,ruleFactory);
	
		List<Account> acounts = a_repo.findAll();
		for (Account account : acounts) {
			if (account.getAccountName().contains("TD_EVERY_DAY"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.ASSET,false);
			if (account.getAccountName().contains("VISA"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.LIABILITY,false);
		}
		l.createRules();
		List<com.example.springboot.accounting.model.entities.Transaction> lt = t_repo.findAll();
		for (com.example.springboot.accounting.model.entities.Transaction transaction : lt) {
			SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy");
			String date = "";
			if (null == transaction.getDate()) {
				System.err.println();
			} else {
				date = sdf.format(transaction.getDate());
			
				String message = transaction.getDescription();
				String mo = transaction.getNote();
				String amount = "" + transaction.getAmount();
				String type = transaction.getTransactionNature().name();
				String cath = "Unknown";
				if (transaction.getType() != null)
					cath = transaction.getType().name();
				String acc = transaction.getAccount();
				l.addTransaction(date, message, mo, amount, type, cath, acc);
			}
		}
		
		List<Asset> assets = assetRepository.findAll();
		for (Asset asset : assets) {
			Amortisation amort = asset.getAmortisation();
			List<AmortisationLeg> legs = amort.getDepreciationLegs();
			for (AmortisationLeg leg : legs) {
				SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy");
				Date d = profileService.getProfile().getFiscalYearEnd().getLastDayDate(leg.getFiscalYear());
				String date = sdf.format(d);
				
				String message = leg.getAmortisation().getAsset().getPurchaceTransaction().getDescription()+"-"+leg.getFiscalYear();
				String mo = "";
				String amount = "" + leg.getAmount();
				String type = "Debit";
				String cath = "Depreciation";
			
				String acc = "5235425";
				l.addTransaction(date, message, mo, amount, type, cath, acc);
			}
		}
		List<LedgerEntryDTO> list = new ArrayList<LedgerEntryDTO>();
		list = convertToLedgerEntryDTOs(l.getTransactions());
		
		
	
		
		Comparator<LedgerEntryDTO> comparator= new Comparator<LedgerEntryDTO>() {

			@Override
			public int compare(LedgerEntryDTO o1, LedgerEntryDTO o2) {
				return o1.getDate().compareTo(o2.getDate());
			}};
			
		Collections.sort(list,comparator);
		return list;
	}
	
	public  List<LedgerEntryDTO> convertToLedgerEntryDTOs(Collection<Transaction> transactions) {
		List<LedgerEntryDTO> ledgerEntryDTOs = new ArrayList<>();

		for (Transaction transaction : transactions) {
			for (TransactionEntry entry : transaction.getEntries()) {
				LedgerEntryDTO ledgerEntryDTO = new LedgerEntryDTO();
				ledgerEntryDTO.setMessage(transaction.getDescription());

				if (entry.getDate() == null) {
					System.err.println();
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.FRENCH);
					Date date;
					try {
						String dateStr= entry.getDate();
						dateStr = dateStr.replace("juill.", "juil.");
						date = sdf.parse(dateStr);
						
						ledgerEntryDTO.setDate(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				if (entry.getAccount() != null) {
					ledgerEntryDTO.setAccountType(entry.getAccount().getAccountType().name());
					ledgerEntryDTO.setGlAccountName(entry.getAccount().getName());
					ledgerEntryDTO.setGlAccountNumber(entry.getAccount().getAccountNumber());
				}

				ledgerEntryDTO.setVendorOrClient(entry.getVendor_client());

				// Set either Debit or Credit based on the entry type
				if (entry.getType() == EntryType.DEBIT) {
					ledgerEntryDTO.setDebit(entry.getAmount());
				} else if (entry.getType() == EntryType.CREDIT) {
					ledgerEntryDTO.setCredit(entry.getAmount());
				}

				ledgerEntryDTOs.add(ledgerEntryDTO);
			}
		}
		return ledgerEntryDTOs;
	}
	

	private void createAccounts(AccountManager accountManager) {
		// Assets
		accountManager.addAccount("FONDS MUTUELS TD", "xxxx", AccountType.ASSET,false);

		accountFactory.createAccounts(accountManager);
	
	}
}
