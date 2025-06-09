package com.sam.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.AmortisationLeg;

public interface AmortisationLegRepository extends JpaRepository<AmortisationLeg, Long>{

}
