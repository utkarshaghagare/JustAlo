package com.JustAlo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    private double amount;
    private String currency;
    private LocalDateTime timestamp;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "transactions"})
    private User user;

    public Transaction(String transactionId, double amount, String currency, LocalDateTime now, String status, User user) {
    }
}
