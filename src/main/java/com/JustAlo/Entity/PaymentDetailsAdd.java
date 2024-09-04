package com.JustAlo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class PaymentDetailsAdd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long accountNo;
    public String bankName;
    public String accountHolderName;
    public String IFSC_Code;
    public String Branch_Name;
    public  String vendor_name;

}
