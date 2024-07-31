package com.JustAlo.Controller;

//import com.JustAlo.Configuration.AmazonS3Config;
//import com.JustAlo.Configuration.AmazonS3Service;
import com.JustAlo.Entity.Bus;
import com.JustAlo.Entity.Driver;
import com.JustAlo.Model.DriverModel;
import com.JustAlo.Service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private PasswordEncoder passwordEncoder;



   // private final DriverService driverService;

//    @Autowired
//    public DriverController(DriverService driverService) {
//        this.driverService = driverService;
//    }

    @PostMapping("/addDriver")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<DriverModel> addCategory(@RequestParam("driverName") String driverName,
                                            @RequestParam("email") String email,
                                            @RequestParam("password") String password,
                                            @RequestParam("mobileNo") String mobileNo,
                                            @RequestParam("licenseNo") String licenseNo,
                                            @RequestParam("address") String address,
                                            @RequestParam("aadharNo") String aadharNo,
                                            @RequestParam("driver_img") MultipartFile driver_img,
                                            @RequestParam("id_proof_img") MultipartFile id_proof_img,
                                            @RequestParam("license_img") MultipartFile license_img,
                                            @RequestParam("verificationStatus") Boolean verificationStatus
                                                ) {
        try {
            String imageUrl = driverService.uploadFile(driver_img);
            String imageUrl1 = driverService.uploadFile(id_proof_img);
            String imageUrl2 = driverService.uploadFile(license_img);

            DriverModel driverModel=new DriverModel();
            driverModel.setDriverName(driverName);
            driverModel.setEmail(email);
            driverModel.setPassword(passwordEncoder.encode(password));
            driverModel.setMobileNo(mobileNo);
            driverModel.setLicenseNo(licenseNo);
            driverModel.setAddress(address);
            driverModel.setAadharNo(aadharNo);
            driverModel.setDriverImg(imageUrl);
            driverModel.setLicenseImg(imageUrl1);
            driverModel.setLicenseImg(imageUrl2);
            driverModel.setVerificationStatus(verificationStatus);
            Driver savedDriver = driverService.addDriver(driverModel);

            return ResponseEntity.ok(driverModel);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }


//    @Autowired
//    private AmazonS3Config amazonS3Config;
//
//    @Autowired
//    private AmazonS3Service amazonS3Service;

//    @PostMapping("/add")
//    @PreAuthorize("hasRole('Vendor')")
//    public ResponseEntity<String> addDriver(@ModelAttribute DriverModel driverModel) {
//        try {
//            driverService.addDriver(driverModel);
//            return ResponseEntity.ok("Driver added successfully");
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Error adding driver: " + e.getMessage());
//        }
//    }
//    @PostMapping("/addDriver")
//    @PreAuthorize("hasRole('Vendor')")
//
//    public ResponseEntity<Driver> addDriver(@RequestParam("driverName") String driverName,
//                                            @RequestParam("email") String email,
//                                            @RequestParam("password") String password,
//                                            @RequestParam("mobileNo") String mobileNo,
//                                            @RequestParam("licenseNo") String licenseNo,
//                                            @RequestParam("address") String address,
//                                            @RequestParam("aadharNo") String aadharNo,
////                                            @RequestParam("driver_img") MultipartFile driver_img,
////                                            @RequestParam("id_proof_img") MultipartFile id_proof_img,
////                                            @RequestParam("license_img") MultipartFile license_img,
//                                            @RequestParam("verificationStatus") Boolean verificationStatus) throws IOException {
////
////            String driverImgUrl = amazonS3Service.uploadFile(driver_img);
////            String idProofImgUrl = amazonS3Service.uploadFile(id_proof_img);
////            String licenseImgUrl = amazonS3Service.uploadFile(license_img);
//        DriverModel driverDTO = new DriverModel();
//        driverDTO.setDriverName(driverName);
//        driverDTO.setEmail(email);
//        driverDTO.setPassword(password);
//        driverDTO.setMobileNo(mobileNo);
//        driverDTO.setLicenseNo(licenseNo);
//        driverDTO.setAddress(address);
////        driverDTO.setAadharNo(aadharNo);
////            driverDTO.setDriverImg(driverImgUrl);
////            driverDTO.setIdProofImg(idProofImgUrl);
////            driverDTO.setLicenseImg(licenseImgUrl);
//        driverDTO.setVerificationStatus(verificationStatus);
//
//        Driver savedDriver = driverService.addDriver(driverDTO);
//        return ResponseEntity.ok(savedDriver);
//    }


//    @PostMapping("/addDriver")
//    @PreAuthorize("hasRole('Vendor')")
//    public ResponseEntity<Driver> addDriver(@RequestBody DriverModel driverDTO) throws IOException {
//
////        String driverImgUrl = amazonS3Service.uploadFile(driverDTO.getDriverImg());
////        String idProofImgUrl = amazonS3Service.uploadFile(driverDTO.getIdProofImg());
////        String licenseImgUrl = amazonS3Service.uploadFile(driverDTO.getLicenseImg());
//
////        driverDTO.setDriverImg(driverImgUrl);
////        driverDTO.setIdProofImg(idProofImgUrl);
////        driverDTO.setLicenseImg(licenseImgUrl);
//
//        Driver savedDriver = driverService.addDriver(driverDTO);
//        return ResponseEntity.ok(savedDriver);
//    }
    @GetMapping("/getAllDriver")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<List<Driver>> getAllBuses() {
        List<Driver> drivers = driverService.getAlldriver();
        return ResponseEntity.ok(drivers);
    }

    @PutMapping("/updatedriver/{id}")
    public ResponseEntity<Driver> updateBus(@PathVariable Long id, @RequestBody Driver driver) {
        Driver updateDriver = driverService.updateDriver(id, driver);
        return ResponseEntity.ok(updateDriver);
    }
    @DeleteMapping("/drivedelete/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('Vendor')")
    @GetMapping("/tobeVerifiedDriver")
    public List<Driver> tobeVerifiedDriver(){
        return driverService.tobeVerifiedDriver ();
    }

    @PreAuthorize("hasRole('Vendor')")
    @PostMapping("/markVerifiedDriver/{id}")
    public Driver markVerifiedDriver(@PathVariable("id") Long id) throws Exception {
        return driverService.markVerifiedDriver(id);
    }//? not workin
    @GetMapping("/verified")
    public ResponseEntity<List<Driver>> getAllVerifiedDriver(@RequestBody DriverModel driverModel){
        List<Driver> drivers= driverService.getAllVerifiedDriver(driverModel);
        return ResponseEntity.ok(drivers);
    }
}
