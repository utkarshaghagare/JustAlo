package com.JustAlo.Repo;

import com.JustAlo.Entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    PaymentEntity findBytransactionId(String transactionId);
}
