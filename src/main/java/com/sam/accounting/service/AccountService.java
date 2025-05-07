package com.sam.accounting.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.accounting.model.dto.AccountDTO;
import com.sam.accounting.model.entities.Account;
import com.sam.accounting.model.entities.FixAccountInfo;
import com.sam.accounting.model.entities.qb.AccountManager;
import com.sam.accounting.model.entities.qb.AccountType;
import com.sam.accounting.repository.AccountRepository;

@Service
public class AccountService {

	//@Autowired
	private AccountFactory factory;

	//@Autowired
	public AccountManager accountManager;

	//@Autowired
	public AccountRepository a_repo;

	private FixAccountInfo fixAccountInfo;
   
	
	@Autowired
	public AccountService(
			AccountFactory factory,
			AccountManager accountManager,
			AccountRepository a_repo,
			FixAccountInfo accountInfo
			) 
	{
		this.factory=factory;
		this.accountManager=accountManager;
		this.a_repo=a_repo;
		this.fixAccountInfo=accountInfo;
		initiazeAccounts();
	}
	
	
	public void initiazeAccounts() 
	{
		factory.createAccounts(accountManager);
		List<Account> acounts = a_repo.findAll();
		for (Account account : acounts) {
			if (account.getAccountName().contains("TD_EVERY_DAY"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.ASSET, false);
			if (account.getAccountName().contains("VISA"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.LIABILITY,
						false);
			if (account.getAccountNo().equals("1"))
				accountManager.addAccount(account.getAccountName(), account.getAccountNo(), AccountType.EQUITY,
						false);
		}
		accountManager.addAccount(
				fixAccountInfo.personalAccount.accountName, 
				fixAccountInfo.personalAccount.accountNo, AccountType.EQUITY, false);
		
		accountManager.addAccount(
				fixAccountInfo.investmentAccount.accountName, 
				fixAccountInfo.investmentAccount.accountNo, AccountType.ASSET, false);
	}
	
	public List<com.sam.accounting.model.entities.qb.Account> findAll(){
		return accountManager.getAccounts();
   }
	// Method to retrieve all accounts and return them as DTOs
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts =  a_repo.findAll(); // Fetch all accounts from the repository

        // Convert each Account entity to AccountDTO and collect them into a list
        return accounts.stream()
                       .map(this::convertToDTO) // Convert each account to DTO
                       .collect(Collectors.toList());
    }
	
	// Helper method to convert Account entity to AccountDTO
    private AccountDTO convertToDTO(Account account) {
    	if(account.getType()== null)
    	{
    		if(account.getAccountName().equals("TD_EVERY_DAY_A_BUSINESS_PLAN"))
    		{	
    			account.setType(AccountType.ASSET.name());
    			account.setAccountingType(AccountType.ASSET);
    		}
    	}
        return new AccountDTO(
            account.getId(),
            account.getType(),
            account.getAccountNo(),
            account.getAccountName(),
            account.getAlias(),
            account.getBalance(),
            account.getAccountingType() != null ? account.getAccountingType().name() : null // Convert Enum to String
        );
    }
    
	public List<Account> getAccountsByType(AccountType type) {

		return accountManager.getAccountByType(type);
	}

	public double getTotalByType(AccountType type) {
		return accountManager.getAccountByType(type).stream().mapToDouble(Account::getBalance).sum();
	}

	public List<Account> getTaxableAccountsByType(AccountType expense) {
		return accountManager.getAccountByType(expense, true);
	}

	public double getTotalNonTaxableByType(AccountType type) {
		return getTotalByTypes(type, false);
	}

	private double getTotalByTypes(AccountType type, boolean isTaxable) {
		return accountManager.getAccountByType(type, isTaxable).stream().mapToDouble(Account::getBalance).sum();
	}

	public double getTotalTaxableByType(AccountType type) {
		return getTotalByTypes(type, true);
	}

	public List<Account> getNonTaxableAccountsByType(AccountType expense) {
		return accountManager.getAccountByType(expense, false);
	}

}
