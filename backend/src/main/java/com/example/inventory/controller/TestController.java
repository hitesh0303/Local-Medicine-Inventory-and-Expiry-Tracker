package com.example.inventory.controller;

import com.example.inventory.model.Medicine;
import com.example.inventory.model.User;
import com.example.inventory.repository.MedicineRepository;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    private static final Logger LOGGER = Logger.getLogger(TestController.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Test controller is working!");
        response.put("timestamp", LocalDate.now());
        response.put("users_count", userRepository.count());
        response.put("medicines_count", medicineRepository.count());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/send-test-email")
    public ResponseEntity<Map<String, Object>> sendTestEmail(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            String name = request.get("name");
            
            if (email == null || name == null) {
                response.put("error", "Email and name are required");
                return ResponseEntity.badRequest().body(response);
            }

            String subject = "Test Email from Local Inventory Tracker";
            String body = String.format("""
                Hello %s,
                
                This is a test email from your Local Inventory Tracker application.
                
                Test Details:
                - Application: Local Inventory Tracker
                - Backend: Spring Boot
                - Frontend: React.js
                - Database: MySQL
                - Email Service: Gmail SMTP
                
                If you receive this email, it means the email service is working correctly!
                
                Best regards,
                Local Inventory Tracker System
                """, name);

            emailService.sendEmail(email, subject, body);
            
            response.put("success", true);
            response.put("message", "Test email sent successfully to " + email);
            response.put("timestamp", LocalDate.now());
            
            LOGGER.info("Test email sent successfully to: " + email);
            
        } catch (Exception e) {
            LOGGER.severe("Failed to send test email: " + e.getMessage());
            response.put("error", "Failed to send test email: " + e.getMessage());
            response.put("details", e.toString());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-test-medicine")
    public ResponseEntity<Map<String, Object>> createTestMedicine(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = request.get("username");
            if (username == null) {
                response.put("error", "Username is required");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                response.put("error", "User not found: " + username);
                return ResponseEntity.badRequest().body(response);
            }

            Medicine medicine = new Medicine();
            medicine.setName("Test Medicine - " + System.currentTimeMillis());
            medicine.setQuantity(10);
            medicine.setBatchNumber("TEST-" + System.currentTimeMillis());
            medicine.setExpiryDate(LocalDate.now().plusDays(5)); // Expires in 5 days
            medicine.setManufacturer("Test Manufacturer");
            medicine.setUser(user);

            Medicine savedMedicine = medicineRepository.save(medicine);
            
            response.put("success", true);
            response.put("message", "Test medicine created successfully");
            response.put("medicine_id", savedMedicine.getId());
            response.put("medicine_name", savedMedicine.getName());
            
        } catch (Exception e) {
            LOGGER.severe("Failed to create test medicine: " + e.getMessage());
            response.put("error", "Failed to create test medicine: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-expired-medicine")
    public ResponseEntity<Map<String, Object>> createExpiredMedicine(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = request.get("username");
            if (username == null) {
                response.put("error", "Username is required");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                response.put("error", "User not found: " + username);
                return ResponseEntity.badRequest().body(response);
            }

            Medicine medicine = new Medicine();
            medicine.setName("Expired Test Medicine - " + System.currentTimeMillis());
            medicine.setQuantity(5);
            medicine.setBatchNumber("EXPIRED-" + System.currentTimeMillis());
            medicine.setExpiryDate(LocalDate.now().minusDays(5)); // Expired 5 days ago
            medicine.setManufacturer("Test Manufacturer");
            medicine.setUser(user);

            Medicine savedMedicine = medicineRepository.save(medicine);
            
            response.put("success", true);
            response.put("message", "Expired test medicine created successfully");
            response.put("medicine_id", savedMedicine.getId());
            response.put("medicine_name", savedMedicine.getName());
            response.put("expiry_date", savedMedicine.getExpiryDate());
            
        } catch (Exception e) {
            LOGGER.severe("Failed to create expired test medicine: " + e.getMessage());
            response.put("error", "Failed to create expired test medicine: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trigger-expiry-check")
    public ResponseEntity<Map<String, Object>> triggerExpiryCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all users
            List<User> users = userRepository.findAll();
            int totalEmailsSent = 0;
            
            for (User user : users) {
                // Get medicines expiring within 7 days for this user
                LocalDate today = LocalDate.now();
                LocalDate expiryAlertDate = today.plusDays(7);
                
                List<Medicine> userMedicines = medicineRepository.findByUserIdAndExpiryDateBetween(
                    user.getId(), today, expiryAlertDate);
                
                if (!userMedicines.isEmpty()) {
                    // Create personalized email content
                    StringBuilder emailText = new StringBuilder();
                    emailText.append("Dear ").append(user.getFullName()).append(",\n\n");
                    emailText.append("The following medicines in your inventory are expiring within the next 7 days:\n\n");
                    
                    for (Medicine medicine : userMedicines) {
                        emailText.append("• Name: ").append(medicine.getName())
                                .append("\n  Batch Number: ").append(medicine.getBatchNumber())
                                .append("\n  Quantity: ").append(medicine.getQuantity())
                                .append("\n  Expiry Date: ").append(medicine.getExpiryDate())
                                .append("\n  Manufacturer: ").append(medicine.getManufacturer() != null ? medicine.getManufacturer() : "N/A")
                                .append("\n\n");
                    }
                    
                    emailText.append("Please take necessary action to manage these medicines before they expire.\n\n");
                    emailText.append("Best regards,\nMedicine Inventory System\n");

                    // Send email to the user
                    emailService.sendEmail(user.getEmail(), "Medicine Expiry Alert - " + user.getFullName(), emailText.toString());
                    totalEmailsSent++;
                    
                    LOGGER.info("Expiry alert email sent to " + user.getEmail() + " for " + userMedicines.size() + " medicines");
                }
            }
            
            response.put("success", true);
            response.put("message", "Expiry check completed");
            response.put("total_users", users.size());
            response.put("emails_sent", totalEmailsSent);
            
        } catch (Exception e) {
            LOGGER.severe("Failed to trigger expiry check: " + e.getMessage());
            response.put("error", "Failed to trigger expiry check: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
} 