package com.example.springboot.accounting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.accounting.model.entities.Supplier;
import com.example.springboot.accounting.repository.SupplierRepository;

@Service
public class SupplierService {

	private SupplierRepository supplierRepository;

	@Autowired
	public SupplierService(SupplierRepository supplierRepository)
	{
		this.supplierRepository = supplierRepository;
	}
	
	public List<Supplier> getAllSuppliers() {
		return supplierRepository.findAll();
	}

	public Supplier createSupplier(Supplier supplier) {
		return supplierRepository.save(supplier);
	}

	public Supplier getSupplierById(Long id) {
		return supplierRepository.findById(id).get();
	}

	public void deleteSupplier(Long id) {
		supplierRepository.deleteById(id);
		
	}

	/**
	 * 
	 * @param id
	 * @param updatedSupplier
	 * @return
	 * @throws Exception
	 */
	public Supplier updateSupplier(Long id, Supplier updatedSupplier) throws Exception {
		Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
		
		if (optionalSupplier.isPresent()) {
			Supplier existingSupplier = optionalSupplier.get();

			// Updating fields from the updatedSupplier object
			existingSupplier.setName(updatedSupplier.getName());
			existingSupplier.setAddress(updatedSupplier.getAddress());
			existingSupplier.setContactEmail(updatedSupplier.getContactEmail());
			existingSupplier.setContactPhone(updatedSupplier.getContactPhone());
			existingSupplier.setNoTps(updatedSupplier.getNoTps());
			existingSupplier.setNoTvq(updatedSupplier.getNoTvq());

			// Save the updated supplier to the database
			return supplierRepository.save(existingSupplier);
		} else {
			// Handle the case where no supplier with the given ID exists
			throw new Exception("Supplier not found with id " + id);
		}
		
	}

}
