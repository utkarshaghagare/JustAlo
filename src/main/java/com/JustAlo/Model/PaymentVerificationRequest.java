package com.JustAlo.Model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentVerificationRequest {
    private String transactionId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    // Getters and Setters
}
