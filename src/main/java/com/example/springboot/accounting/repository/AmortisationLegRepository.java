package com.example.springboot.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot.accounting.model.entities.AmortisationLeg;

public interface AmortisationLegRepository extends JpaRepository<AmortisationLeg, Long>{

}
