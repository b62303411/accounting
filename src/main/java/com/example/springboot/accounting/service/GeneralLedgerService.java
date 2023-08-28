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
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;
import com.example.springboot.accounting.model.entities.qb.EntryType;
import com.example.springboot.accounting.model.entities.qb.Ledger;
import com.example.springboot.accounting.model.entities.qb.LedgerRuleFactory;
import com.example.springboot.accounting.model.entities.qb.Transaction;
import com.example.springboot.accounting.model.entities.qb.TransactionEntry;
import com.example.springboot.accounting.repository.AccountRepository;
import com.example.springboot.accounting.repository.AssetRepository;
import com.example.springboot.accounting.repository.InvoiceRepository;
import com.example.springboot.accounting.repository.TransactionRepository;

@Service
public class GeneralLedgerService {

	@Autowired
	public InvoiceRepository i_repo;

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

	private List<LedgerEntryDTO> cashedLedger;

	public GeneralLedgerService() {
		cashedLedger = new ArrayList<LedgerEntryDTO>();
	}

	Comparator<LedgerEntryDTO> comparator = new Comparator<LedgerEntryDTO>() {

		@Override
		public int compare(LedgerEntryDTO o1, LedgerEntryDTO o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	};

	public Ledger populateLedger() {
		createAccounts(accountManager);
		l = new Ledger(accountManager, ruleFactory);

		List<Account> acounts = a_repo.findAll();
		for (Account account : acounts) {
			if (account.getAccountName().contains("TD_EVERY_DAY"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.ASSET, false);
			if (account.getAccountName().contains("VISA"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.LIABILITY,
						false);
		}
		l.createRules();

		populateFromTransaction();

		populateFromAsset();

		populateFromInvoices();

		return l;
	}

	private void populateFromInvoices() {
		List<Invoice> list = i_repo.findAllByOrigine("Cloutier & Longtin");
		for (Invoice invoice : list) {
			SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy");
			Date d = invoice.getDate();
			String date = sdf.format(d);

			String message = "I " + invoice.getOrigine() + ":" + invoice.getNoFacture();
			String mo = invoice.getNoFacture();
			String amount = "" + invoice.getAmount();
			String type = "Debit";
			String cath = "Invoice";

			String acc = "5235425";
			l.addTransaction(date, message, mo, amount, type, cath, acc,null);
		}

	}

	private void populateFromAsset() {
		List<Asset> assets = assetRepository.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy");
		for (Asset asset : assets) {
			Amortisation amort = asset.getAmortisation();
			List<AmortisationLeg> legs = amort.getDepreciationLegs();
			Date d = null;
			for (AmortisationLeg leg : legs) {

				d = profileService.getProfile().getFiscalYearEnd().getLastDayDate(leg.getFiscalYear());
				String date = sdf.format(d);

				String message = leg.getAmortisation().getAsset().getPurchaceTransaction().getDescription() + "-"
						+ leg.getFiscalYear();
				String mo = "";
				String amount = "" + leg.getAmount();
				String type = "Debit";
				String cath = "Depreciation";

				String acc = "5235425";
				l.addTransaction(date, message, mo, amount, type, cath, acc,null);
			}
			if (d != null) {
				String message = asset.getPurchaceTransaction().getDescription() + ":Lost Of Asset Write Off";
				String cath = "LostOfAssetWriteOff";
				String type = "Debit";
				String acc = "5235425";
				String amount = asset.getCurrentValue() + "";
				String mo = "";
				String date = sdf.format(d);
				l.addTransaction(date, message, mo, amount, type, cath, acc,null);
			}
		}
	}

	private void populateFromTransaction() {
		List<com.example.springboot.accounting.model.entities.Transaction> lt = t_repo.findAll();
		Comparator<com.example.springboot.accounting.model.entities.Transaction> comp= new Comparator<com.example.springboot.accounting.model.entities.Transaction>() {
			
			@Override
			public int compare(com.example.springboot.accounting.model.entities.Transaction o1,
					com.example.springboot.accounting.model.entities.Transaction o2) {
				if(o1.getDate()==null || o2.getDate() == null)
				{
					System.err.println();
					
				}
				return o1.getDate().compareTo(o2.getDate());
			}
		};
		Collections.sort(lt,comp);
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
				Double solde = transaction.getSolde();
				l.addTransaction(date, message, mo, amount, type, cath, acc,solde);
			}
		}
	}

	public List<LedgerEntryDTO> getLedgerDtos() {

		if (cashedLedger.isEmpty()) {
			this.cashedLedger = extractAndPopulateLedger();

			Collections.sort(cashedLedger, comparator);
		}
		return cashedLedger;
	}

	private List<LedgerEntryDTO> extractAndPopulateLedger() {
		l = populateLedger();

		List<LedgerEntryDTO> list = new ArrayList<LedgerEntryDTO>();
		list = convertToLedgerEntryDTOs(l.getTransactions());
		return list;
	}

	public List<LedgerEntryDTO> convertToLedgerEntryDTOs(Collection<Transaction> transactions) {
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
						String dateStr = entry.getDate();
						dateStr = dateStr.replace("juill.", "juil.");
						date = sdf.parse(dateStr);
						ledgerEntryDTO.setBalence(entry.getBalance());
						ledgerEntryDTO.setAbalence(entry.getActualBalence());
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
		accountManager.addAccount("FONDS MUTUELS TD", "9115997", AccountType.ASSET, false);

		accountFactory.createAccounts(accountManager);

	}
}
