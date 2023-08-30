package com.example.springboot.accounting.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.dto.LedgerEntryDTO;
import com.example.springboot.accounting.model.entities.qb.EntryType;
import com.example.springboot.accounting.model.entities.qb.Transaction;
import com.example.springboot.accounting.model.entities.qb.TransactionEntry;

@Service
public class LedgerTransactionToDto {
	public List<LedgerEntryDTO> convertToLedgerEntryDTOs(Collection<Transaction> transactions) {
		List<LedgerEntryDTO> ledgerEntryDTOs = new ArrayList<>();

		for (Transaction transaction : transactions) {
			for (TransactionEntry entry : transaction.getEntries()) {
				LedgerEntryDTO ledgerEntryDTO = new LedgerEntryDTO();
				ledgerEntryDTO.setMessage(transaction.getDescription());

				if (entry.getDate() == null) {
					System.err.println();
				} else {
					//SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.FRENCH);
					//Date date;
//					try {
						//dateStr = entry.getDate();
						//dateStr = dateStr.replace("juill.", "juil.");
						//date = sdf.parse(dateStr);
						ledgerEntryDTO.setBalence(entry.getBalance());
						ledgerEntryDTO.setAbalence(entry.getActualBalence());
						ledgerEntryDTO.setDate(entry.getDate());
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

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
}
