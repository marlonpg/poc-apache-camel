package com.example.camel;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private int callCount = 0;

    public String processPayment(Order order) {
        callCount++;
        System.out.println("PaymentService called - Attempt #" + callCount + " for " + order);

        // Simulate failure on first 2 attempts, succeed on 3rd
        if (callCount < 3) {
            throw new RuntimeException("Payment service temporarily unavailable");
        }

        callCount = 0; // Reset for next order
        return "Payment processed successfully for order: " + order.getId();
    }
}
