package com.example.inventory.repository;

import com.example.inventory.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    // Custom query to find medicines expiring within a given date range
    List<Medicine> findByExpiryDateBetween(LocalDate start, LocalDate end);
    
    // User-specific queries
    List<Medicine> findByUserId(Long userId);
    List<Medicine> findByUserIdAndExpiryDateBetween(Long userId, LocalDate start, LocalDate end);
}