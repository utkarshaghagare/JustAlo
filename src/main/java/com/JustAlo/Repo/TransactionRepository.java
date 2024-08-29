package com.JustAlo.Repo;

import com.JustAlo.Entity.OrdinaryTrip;
import com.JustAlo.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
