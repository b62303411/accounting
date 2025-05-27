package com.sam.accounting.model;

import java.util.List;

import com.sam.accounting.model.dto.LedgerEntryDTO;
import com.sam.accounting.model.entities.qb.Account;


public class AccountLedger {
	public Account account;
	public List<LedgerEntryDTO> entries;
}
