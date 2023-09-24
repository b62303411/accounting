package com.example.springboot.accounting.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.TransactionNature;
import com.example.springboot.accounting.model.TransactionType;
import com.example.springboot.accounting.model.dto.LedgerEntryDTO;
import com.example.springboot.accounting.model.entities.Amortisation;
import com.example.springboot.accounting.model.entities.AmortisationLeg;
import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.model.entities.FixAccountInfo;
import com.example.springboot.accounting.model.entities.Invoice;
import com.example.springboot.accounting.model.entities.qb.AccountManager;
import com.example.springboot.accounting.model.entities.qb.AccountType;
import com.example.springboot.accounting.model.entities.qb.Ledger;
import com.example.springboot.accounting.repository.AssetRepository;
import com.example.springboot.accounting.repository.InvoiceRepository;
import com.example.springboot.accounting.repository.RuleRepository;
import com.example.springboot.accounting.repository.TransactionRepository;

@Service
public class GeneralLedgerService implements PropertyChangeListener {

	@Autowired
	private FiscalYearService fys;

	@Autowired
	private InvoiceRepository i_repo;

	@Autowired
	private TransactionRepository t_repo;

	@Autowired
	private AccountFactory accountFactory;

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private AssetRepository assetRepository;

	@Autowired
	private CompanyProfileService profileService;
	
	@Autowired
	private LedgerTransactionToDto dtoParser;
	
	@Autowired
	private SimplifiedSalesTaxesService ssts;
	
	@Autowired
	private RuleRepository ruleRepo;
		
	@Autowired
	private FixAccountInfo fixAccountInfo;
	
	@Autowired
	private GeneralLedger gl;
	
	@Autowired
	private IncomeStatementFromLedgerService isfls;
	
	private Ledger ledger;

	private List<LedgerEntryDTO> cashedLedger;

	public GeneralLedgerService() {
		cashedLedger = new ArrayList<LedgerEntryDTO>();
	}

	public Ledger getLedger() 
	{
		if(null == ledger) 
		{
			
			populateLedger();
		}
		ledger.recalculateLedger();
		return ledger;
	}
	
	Comparator<LedgerEntryDTO> comparator = new Comparator<LedgerEntryDTO>() {

		@Override
		public int compare(LedgerEntryDTO o1, LedgerEntryDTO o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	};
	
	public void rePopulateLedger() {
		// TODO Auto-generated method stub
		
	}
	
	public Ledger populateLedger() {
		createAccounts(accountManager);
	
		ledger = gl.getLedger();

		ledger.addObserver(this);
		
		ruleRepo.createRules();

		populateFromTransaction();

		populateFromAsset();

		populateFromInvoices();
		
		populateFromSimplifiedMethod();
		
		populateFromIncomeTaxes();

		return ledger;
	}

	private void populateFromIncomeTaxes() {
		isfls.populateMap();
	}

	private void populateFromSimplifiedMethod() {
		ssts.run(ledger.getSeq(),ledger.getTransactions(),ledger);
		
	}

	private void populateFromInvoices() {
		List<Invoice> list = i_repo.findAllByOrigine("Cloutier & Longtin");
		populateInvoices(list);
		
		list = i_repo.findAllByOrigine("MTA");
		populateInvoices(list);

	}

	private void populateInvoices(List<Invoice> list) {
		for (Invoice invoice : list) {
			SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy");
			Date d = invoice.getDate();
			String date = sdf.format(d);

			String message = "I " + invoice.getOrigine() + ":" + invoice.getNoFacture();
			String mo = invoice.getNoFacture();
			String amount = "" + invoice.getAmount();
			TransactionNature type = TransactionNature.Debit;
			TransactionType cath = TransactionType.Invoice;

			String acc = fixAccountInfo.checkingAccount.accountNo;
			
			ledger.addTransaction(d , message, mo, amount, type, cath, acc,null);
		}
	}

	private void populateFromAsset() {
		List<Asset> assets = assetRepository.findAll();
		//SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy");
		for (Asset asset : assets) {
			Amortisation amort = asset.getAmortisation();
			List<AmortisationLeg> legs = amort.getDepreciationLegs();
			Date d = null;
			for (AmortisationLeg leg : legs) {

				d = profileService.getProfile().getFiscalYearEnd().getLastDayDate(leg.getFiscalYear());
				//String date = sdf.format(d);

				String message = leg.getAmortisation().getAsset().getPurchaceTransaction().getDescription() + "-"
						+ leg.getFiscalYear();
				String mo = "";
				String amount = "" + leg.getAmount();
				TransactionNature type = TransactionNature.Debit;
				TransactionType cath = TransactionType.Depreciation;

				String acc = fixAccountInfo.checkingAccount.accountNo;
				ledger.addTransaction(d, message, mo, amount, type, cath, acc,null);
			}
			if (d != null) {
				String message = asset.getPurchaceTransaction().getDescription() + ":Lost Of Asset Write Off";
				TransactionType cath = TransactionType.LostOfAssetWriteOff;
				TransactionNature type = TransactionNature.Debit;
				String acc = fixAccountInfo.checkingAccount.accountNo;
				String amount = asset.getCurrentValue() + "";
				String mo = "";
				//String date = sdf.format(d);
				ledger.addTransaction(d, message, mo, amount, type, cath, acc,null);
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
				TransactionNature type = transaction.getTransactionNature();
				String cath = "Unknown";
				if (transaction.getType() != null)
					cath = transaction.getType().name();
				String acc = transaction.getAccount();
				Double solde = transaction.getSolde();
				ledger.addTransaction(transaction.getDate(), message, mo, amount, type, transaction.getType(), acc,solde);
			}
		}
	}

	public List<LedgerEntryDTO> getLedgerDtos(int fy) {
		List<LedgerEntryDTO> dtos = getLedgerDtos();
		List<LedgerEntryDTO> fyDtos= new ArrayList<LedgerEntryDTO>();
		for (LedgerEntryDTO ledgerEntryDTO : dtos) {
			if(fys.getFiscalYear(ledgerEntryDTO.getDate()) == fy) 
			{
				fyDtos.add(ledgerEntryDTO);
			}
		
		}
		return fyDtos;
	}
	
	public List<LedgerEntryDTO> getLedgerDtos() {

		if (cashedLedger.isEmpty()) {
			this.cashedLedger = extractAndPopulateLedger();

			Collections.sort(cashedLedger, comparator);
		}
		return cashedLedger;
	}

	private List<LedgerEntryDTO> extractAndPopulateLedger() {
		ledger = getLedger();

		List<LedgerEntryDTO> list = new ArrayList<LedgerEntryDTO>();
		list = this.dtoParser.convertToLedgerEntryDTOs(ledger.getTransactions());
		return list;
	}

	

	private void createAccounts(AccountManager accountManager) {
		// Assets
		
		accountManager.addAccount(
				fixAccountInfo.investmentAccount.accountName, 
				fixAccountInfo.investmentAccount.accountNo, AccountType.ASSET, false);

		accountFactory.createAccounts(accountManager);

	}

	public void clearCashedLedger() {
		this.cashedLedger.clear();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		clearCashedLedger();
		
	}




}
