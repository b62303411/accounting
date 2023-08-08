package com.example.springboot.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot.accounting.model.entities.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long>{

}
