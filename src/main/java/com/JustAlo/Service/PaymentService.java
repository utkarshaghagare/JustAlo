package com.JustAlo.Service;

import com.JustAlo.Configuration.RazorpayConfig;
import com.JustAlo.Entity.Booking;
import com.JustAlo.Entity.Transaction;
import com.JustAlo.Entity.User;
import com.JustAlo.Repo.TransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import jakarta.transaction.Transactional;
import netscape.javascript.JSObject;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.codec.digest.HmacUtils.hmacSha256;

@Service
public class PaymentService {

    @Autowired
    private RazorpayClient razorpayClient;
    @Autowired
    private RazorpayConfig razorpayConfig;
    @Autowired
    private TransactionRepository transactionRepository;

    public Order bookedTicket(double amount) throws Exception {
        JSONObject bookedTicket = new JSONObject();
        bookedTicket.put("amount", amount * 100); // Amount in paise
        bookedTicket.put("currency", "INR");
        bookedTicket.put("receipt", "txn_123456");
        bookedTicket.put("payment_capture", 1);


        return razorpayClient.Orders.create(bookedTicket);
    }

    @Transactional

    public Transaction saveTransaction(String transactionId, double amount, String currency, String status, User user) {
        Transaction transaction = new Transaction(transactionId, amount, currency, LocalDateTime.now(), status, user);
        transaction.setTransactionId(transactionId);
        transaction.setAmount(amount);
        transaction.setCurrency("INR");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("success");
        transaction.setUser(user);
        return transactionRepository.save(transaction);

    }

    public ResponseEntity<List<Transaction>> getAllDetails() {
        List<Transaction> transactions = transactionRepository.findAll();
        return ResponseEntity.ok(transactions);
    }
}