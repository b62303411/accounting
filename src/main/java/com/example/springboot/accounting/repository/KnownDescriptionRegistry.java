package com.example.springboot.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot.accounting.model.entities.KnownDescription;

public interface KnownDescriptionRegistry extends JpaRepository<KnownDescription, Long>{

}
