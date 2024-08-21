package com.JustAlo.Service;

import com.JustAlo.Entity.Booking;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private RazorpayClient razorpayClient;

    public Order bookedTicket(double amount) throws Exception{
    JSONObject bookedTicket =new JSONObject();
    bookedTicket.put("amount", amount * 100); // Amount in paise
    bookedTicket.put("currency", "INR");
    bookedTicket.put("receipt", "txn_123456");
    bookedTicket.put("payment_capture", 1);

    return razorpayClient.Orders.create(bookedTicket);
}
}
