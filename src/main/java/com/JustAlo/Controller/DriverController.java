package com.JustAlo.Controller;

import com.JustAlo.Configuration.AmazonS3Config;
import com.JustAlo.Entity.Driver;
import com.JustAlo.Model.DriverModel;
import com.JustAlo.Service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private AmazonS3Config amazonS3Config;

    @PostMapping("/add")
    public ResponseEntity<Driver> addDriver(@RequestParam("driverName") String driverName,
                                            @RequestParam("email") String email,
                                            @RequestParam("password") String password,
                                            @RequestParam("mobileNo") String mobileNo,
                                            @RequestParam("licenseNo") String licenseNo,
                                            @RequestParam("address") String address,
                                            @RequestParam("aadharNo") String aadharNo,
                                            @RequestParam("driver_img") MultipartFile driver_img,
                                            @RequestParam("id_proof_img") MultipartFile id_proof_img,
                                            @RequestParam("license_img") MultipartFile license_img,
                                            @RequestParam("verificationStatus") Boolean verificationStatus) throws IOException {

        try {
            // Upload images to S3 and get URLs
            String driverImgUrl = amazonS3Config.uploadFile(driver_img);
            String idProofImgUrl = amazonS3Config.uploadFile(id_proof_img);
            String licenseImgUrl = amazonS3Config.uploadFile(license_img);

            DriverModel driverDTO = new DriverModel();
        driverDTO.setDriverName(driverName);
        driverDTO.setEmail(email);
        driverDTO.setPassword(password);
        driverDTO.setMobileNo(mobileNo);
        driverDTO.setLicenseNo(licenseNo);
        driverDTO.setAddress(address);
        driverDTO.setAadharNo(aadharNo);
          driverDTO.setDriverImg(driverImgUrl);
          driverDTO.setIdProofImg(idProofImgUrl);
           driverDTO.setLicenseImg(licenseImgUrl);
        driverDTO.setVerificationStatus(verificationStatus);

            Driver newDriver = driverService.addDriver(driverDTO);
            return ResponseEntity.ok(newDriver);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }

    }


    @GetMapping("/getAllDriver")
    public List<Driver> getAllDriver(@RequestBody DriverModel driverModel){
        return driverService.getAllDriver(driverModel);

    }

    @GetMapping("/verifiedDrivers")
    public ResponseEntity<List<Driver>> getAllVerifiedDriver(@RequestBody DriverModel driverModel){
        List<Driver> drivers= driverService.getAllVerifiedDriver(driverModel);
        return ResponseEntity.ok(drivers);
    }
}
