package com.sam.accounting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

	/**
	 * 
	 * @param accountName
	 * @return
	 */
	Optional<Account> findByAccountName(String accountName);

	/**
	 * 
	 * @param acc
	 * @return
	 */
	Optional<Account> findByAccountNo(String acc);

	

}
