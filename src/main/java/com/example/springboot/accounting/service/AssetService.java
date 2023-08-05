package com.example.springboot.accounting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.Asset;
import com.example.springboot.accounting.repository.AssetRepository;

@Service
public class AssetService {
	AssetRepository assetRepository;
	@Autowired
	public AssetService(AssetRepository repo) {assetRepository=repo;}
	public List<Asset> findAll() {
		
		return assetRepository.findAll();
	}
	
	

}
