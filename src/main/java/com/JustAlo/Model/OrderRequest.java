package com.JustAlo.Model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderRequest {
    private Double amount;
    private String currency;
    private long userID;

    // Getters and Setters
}
