package com.example.springboot.accounting.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Liability {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
}
