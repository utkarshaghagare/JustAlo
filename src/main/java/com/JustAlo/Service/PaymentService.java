package com.JustAlo.Service;

import com.JustAlo.Configuration.RazorpayConfig;
import com.JustAlo.Entity.Booking;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import netscape.javascript.JSObject;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static org.apache.commons.codec.digest.HmacUtils.hmacSha256;

@Service
public class PaymentService {

    @Autowired
    private RazorpayClient razorpayClient;
    @Autowired
    private RazorpayConfig razorpayConfig;

    public Order bookedTicket(double amount) throws Exception{
    JSONObject bookedTicket =new JSONObject();
    bookedTicket.put("amount", amount * 100); // Amount in paise
    bookedTicket.put("currency", "INR");
    bookedTicket.put("receipt", "txn_123456");
    bookedTicket.put("payment_capture", 1);

    return razorpayClient.Orders.create(bookedTicket);
}

    public boolean verifyPayment(String orderId, String paymentId, String signature) throws Exception {
        String payload = orderId + "|" + paymentId;
        String expectedSignature = hmacSha256(payload, razorpayConfig.getSecret());

        return expectedSignature.equals(signature);
    }
    private String hmacSha256(String data, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        return Hex.encodeHexString(hash);
    }
}
