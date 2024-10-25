package com.JustAlo.Service;

import com.JustAlo.Configuration.RazorpayConfig;
import com.JustAlo.Entity.Booking;
import com.JustAlo.Entity.PaymentEntity;
import com.JustAlo.Entity.Transaction;
import com.JustAlo.Entity.User;
import com.JustAlo.Model.TransactionDTO;
import com.JustAlo.Repo.PaymentRepository;
import com.JustAlo.Repo.TransactionRepository;
import com.JustAlo.Repo.UserDao;
import com.JustAlo.Security.JwtAuthenticationFilter;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import netscape.javascript.JSObject;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.codec.digest.HmacUtils.hmacSha256;

@Service
public class PaymentService {


    @Autowired
    private RazorpayClient razorpayClient;
    @Autowired
    private RazorpayConfig razorpayConfig;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserDao userDao;

    //
    public PaymentService() throws RazorpayException {
        this.razorpayClient = new RazorpayClient("razorpay.key", "razorpay.secret");
    }

    public Order createOrder(Double amount, String currency, long userId) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // amount in the smallest currency unit
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpayClient.Orders.create(orderRequest);
        User user = new User();
        user.setId(userId);

        String email = JwtAuthenticationFilter.CURRENT_USER;
        User currentUser = userDao.findByEmail(email);

// If the user is null or not found, throw an exception
        if (currentUser == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }

        // Save the order details in the database
        Transaction orderEntity = new Transaction();
        orderEntity.setTransactionId(order.get("id"));
        orderEntity.setCurrency(currency);
        orderEntity.setAmount(amount);
        orderEntity.setStatus("CREATED");
        orderEntity.setTimestamp(LocalDateTime.now());
        orderEntity.setUser(currentUser);
        transactionRepository.save(orderEntity);

        return order;
    }


    public boolean verifyPayment(String transactionId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        // Generate the expected signature using the secret key
        String secret = "YOUR_API_SECRET";
        String payload = transactionId + "|" + razorpayPaymentId;
        String generatedSignature = generateSignature(payload, secret);

        boolean isValid = generatedSignature.equals(razorpaySignature);

        // Save payment details to database
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setTransactionId(transactionId);
        paymentEntity.setRazorpayPaymentId(razorpayPaymentId);
        paymentEntity.setRazorpaySignature(razorpaySignature);
        paymentEntity.setStatus(isValid ? "SUCCESS" : "FAILED");
        paymentRepository.save(paymentEntity);

        return isValid;
    }

    private String generateSignature(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(payload.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }


    //-----------------------ankit code----------------------
//    public Order bookedTicket(double amount) throws Exception {
//        JSONObject bookedTicket = new JSONObject();
//        bookedTicket.put("amount", amount * 100); // Amount in paise
//        bookedTicket.put("currency", "INR");
//        bookedTicket.put("receipt", "txn_123456");
//        bookedTicket.put("payment_capture", 1);
//
//
//        return razorpayClient.Orders.create(bookedTicket);
//    }
//
//    @Transactional
//
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

    public ResponseEntity<List<TransactionDTO>> getAllDetails() {
        List<Transaction> transactions = transactionRepository.findAll();

        // Convert List<Transaction> to List<TransactionDTO>
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transaction -> new TransactionDTO(
                        transaction.getTransactionId(),
                        transaction.getAmount(),
                        transaction.getStatus(),
                        transaction.getUser().getEmail(),  // Assuming `User` has an `email` field
                        transaction.getTimestamp()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactionDTOs);
//    }

    }
}
