package com.example.springboot.accounting.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;

@Service
public class ConsolidationService {

	@Autowired
	EntityManager entityManager; // Your entity manager instance
	Set<Long> existingExpenseIds = new HashSet<>(); // Set of existing expense IDs

	// Populate existingExpenseIds with your existing expense IDs
	public boolean isConsolidated(Long expenseID) {
		String jpqlQuery = "SELECT DISTINCT expense.id " + "FROM Consolidation c " + "JOIN c.asset asset "
				+ "JOIN asset.amortisations amortisation " + "JOIN amortisation.amortisationLegs amortisationLeg "
				+ "JOIN amortisationLeg.expense expense";
		List<Long> expenseIds = entityManager.createQuery(jpqlQuery, Long.class).getResultList();
		return expenseIds.contains(expenseIds);
	}
}
