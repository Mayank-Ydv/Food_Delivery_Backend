package com.foodapp.notification.service;

import org.springframework.stereotype.Service;

/**
 * MOCK implementation — just logs to console for now.
 * To make this real: inject JavaMailSender (Spring Boot Starter Mail)
 * and call mailSender.send(...) here instead of System.out.println.
 */
@Service
public class EmailService {
    public void sendEmail(Long userId, String message) {
        System.out.println("[EMAIL] To user " + userId + ": " + message);
    }
}
