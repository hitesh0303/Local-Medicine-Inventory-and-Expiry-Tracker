package com.example.inventory.service;

import com.example.inventory.model.Medicine;
import com.example.inventory.model.User;
import com.example.inventory.repository.MedicineRepository;
import com.example.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
@EnableScheduling
public class ExpiryCheckService {

    private static final Logger LOGGER = Logger.getLogger(ExpiryCheckService.class.getName());

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Run daily at 9:00 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkExpiringMedicines() {
        LOGGER.info("Starting daily expiry check...");
        
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
            
            LOGGER.info("Daily expiry check completed. Total emails sent: " + totalEmailsSent);
            
        } catch (Exception e) {
            LOGGER.severe("Error during expiry check: " + e.getMessage());
        }
    }
}