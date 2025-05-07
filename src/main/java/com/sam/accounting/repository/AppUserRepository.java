package com.sam.accounting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sam.accounting.model.entities.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
 
   public Optional<AppUser> findByUsername(String username);
}