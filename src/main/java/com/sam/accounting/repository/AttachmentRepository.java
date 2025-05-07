package com.sam.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.accounting.model.entities.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long>{

	Attachment findBySha256Hash(String hash);

}
