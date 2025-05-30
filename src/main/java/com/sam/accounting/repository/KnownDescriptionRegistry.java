package com.sam.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.KnownDescription;

public interface KnownDescriptionRegistry extends JpaRepository<KnownDescription, Long>{

}
