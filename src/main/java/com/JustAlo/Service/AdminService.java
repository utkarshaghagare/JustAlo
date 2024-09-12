package com.JustAlo.Service;

import com.JustAlo.Entity.Admin;
import com.JustAlo.Entity.PaymentDetailsAdd;
import com.JustAlo.Repo.AdminRepository;
import com.JustAlo.Repo.PaymentDetailsAddRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PaymentDetailsAddRepo paymentDetailsAddRepo;
    public List<Admin> getAllVendor() {
        return adminRepository.findAll();
    }

    public ResponseEntity<PaymentDetailsAdd> addPaymenetDetailsAdd(PaymentDetailsAdd paymentDetailsAdd) {
        PaymentDetailsAdd savedPaymentDetailsAdd = paymentDetailsAddRepo.save(paymentDetailsAdd);
        return new ResponseEntity<>(savedPaymentDetailsAdd, HttpStatus.CREATED);
    }
    public List<PaymentDetailsAdd> getAllPaymentDetails() {
        return paymentDetailsAddRepo.findAll();
    }


}
