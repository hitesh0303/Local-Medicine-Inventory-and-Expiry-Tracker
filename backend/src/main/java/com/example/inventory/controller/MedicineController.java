package com.example.inventory.controller;

import com.example.inventory.model.Medicine;
import com.example.inventory.model.User;
import com.example.inventory.repository.MedicineRepository;
import com.example.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "http://localhost:3000")
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private UserService userService;

    // Get all medicines for the current user
    @GetMapping
    public List<Medicine> getAllMedicines() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());
        return medicineRepository.findByUserId(currentUser.getId());
    }

    // Add a new medicine for the current user
    @PostMapping
    public Medicine addMedicine(@RequestBody Medicine medicine) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());
        medicine.setUser(currentUser);
        return medicineRepository.save(medicine);
    }

    // Update an existing medicine (only if it belongs to the current user)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicineDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());
        
        return medicineRepository.findById(id)
                .map(medicine -> {
                    // Check if the medicine belongs to the current user
                    if (!medicine.getUser().getId().equals(currentUser.getId())) {
                        return ResponseEntity.status(403).build(); // Forbidden
                    }
                    
                    // Update the medicine fields
                    medicine.setName(medicineDetails.getName());
                    medicine.setQuantity(medicineDetails.getQuantity());
                    medicine.setBatchNumber(medicineDetails.getBatchNumber());
                    medicine.setExpiryDate(medicineDetails.getExpiryDate());
                    medicine.setManufacturer(medicineDetails.getManufacturer());
                    
                    Medicine updatedMedicine = medicineRepository.save(medicine);
                    return ResponseEntity.ok(updatedMedicine);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Delete a medicine (only if it belongs to the current user)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());
        
        return medicineRepository.findById(id)
                .map(medicine -> {
                    // Check if the medicine belongs to the current user
                    if (!medicine.getUser().getId().equals(currentUser.getId())) {
                        return ResponseEntity.status(403).build(); // Forbidden
                    }
                    
                    medicineRepository.delete(medicine);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}