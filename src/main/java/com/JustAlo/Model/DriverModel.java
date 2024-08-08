package com.JustAlo.Model;

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
    private MultipartFile driverImg;
    private MultipartFile idProofImg;
    private MultipartFile licenseImg;
    private Boolean verificationStatus;

    public void setDriverImg(String driverImgUrl) {
        this.driverImg=driverImg;
    }

    public void setIdProofImg(String idProofImgUrl) {
        this.idProofImg=idProofImg;
    }

    public void setLicenseImg(String licenseImgUrl) {
        this.licenseImg=licenseImg;
    }
}
