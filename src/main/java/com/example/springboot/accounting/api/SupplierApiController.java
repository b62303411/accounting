package com.example.springboot.accounting.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.accounting.model.entities.Supplier;
import com.example.springboot.accounting.service.SupplierService;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierApiController {

    @Autowired
    private SupplierService contactService;

    @GetMapping
    public ResponseEntity<List<Supplier>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllSuppliers());
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(contactService.createSupplier(supplier));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getContact(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getSupplierById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateContact(@PathVariable Long id, @RequestBody Supplier supplier) {
        try {
			return ResponseEntity.ok(contactService.updateSupplier(id, supplier));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
