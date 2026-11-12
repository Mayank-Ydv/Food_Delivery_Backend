package com.foodapp.notification.service;

import org.springframework.stereotype.Service;

/**
 * MOCK implementation — logs to console.
 * To make this real: integrate Twilio SDK and call their API here.
 */
@Service
public class SmsService {
    public void sendSms(Long userId, String message) {
        System.out.println("[SMS] To user " + userId + ": " + message);
    }
}
