package com.JustAlo.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DriverModel {



    private String driverName;
    private String email;
    private String password;
    private String mobileNo;
    private String licenseNo;
    private String address;
    private String aadharNo;
    private String driverImg;
    private String idProofImg;
    private String licenseImg;
    private Boolean verificationStatus;


    public void setDriverImg(String imageUrl) {
    }

    public void setLicenseImg(String imageUrl1) {
    }
}
