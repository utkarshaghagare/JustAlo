package com.JustAlo.Service;
//
//import com.JustAlo.Configuration.AmazonS3Config;
import com.JustAlo.Entity.Bus;
import com.JustAlo.Entity.Driver;
import com.JustAlo.Entity.Role;
import com.JustAlo.Entity.Vendor;
import com.JustAlo.Model.DriverModel;
import com.JustAlo.Repo.DriverDao;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.VendorDao;
import com.JustAlo.Security.JwtAuthenticationFilter;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.*;

import java.nio.file.Files;
import java.nio.file.Path;


@Service
public class DriverService {



    @Autowired
    private DriverDao driverDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private VendorDao vendorDao;


    private final S3Client s3Client;
    private final String bucketName = "studycycle";
    private final String spaceUrl = "https://studycycle.blr1.digitaloceanspaces.com/";

    public DriverService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + StringUtils.cleanPath(file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Path tempFile = Files.createTempFile("upload-", fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // Upload to DigitalOcean Spaces
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .acl("public-read")
                .build();

        s3Client.putObject(putObjectRequest, tempFile);

        // Clean up temporary file
        Files.deleteIfExists(tempFile);

        // Return the file URL
        return spaceUrl + fileName;
    }


    public ResponseEntity<Driver> getAllDriver(DriverModel driverModel) {
        return (ResponseEntity<Driver>) driverDao.findAll();
    }


//    public Driver addDriver(DriverModel driverDTO) {
//        String currentUser = JwtAuthenticationFilter.CURRENT_USER;
//        Vendor vendor= vendorDao.findByUsername(currentUser).get();
//        Driver driver = new Driver();
//
//        driver.setDriver_name(driverDTO.getDriverName());
//        driver.setEmail(driverDTO.getEmail());
//        driver.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
//        driver.setMobile_no(driverDTO.getMobileNo());
//        driver.setLicense_no(driverDTO.getLicenseNo());
//        driver.setAddress(driverDTO.getAddress());
//        driver.setAadhar_no(driverDTO.getAadharNo());
//        driver.setVerification_status(driverDTO.getVerificationStatus());
//        driver.setVendor(vendor);
//
//
//        Role role = roleDao.findById("Driver")
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//        Set<Role> userRoles = new HashSet<>();
//        userRoles.add(role);
//        driver.setRole(userRoles);
//
//        return driverDao.save(driver);
//    }




    public Driver saveDriver(DriverModel driverModel) throws IOException {
                String currentUser = JwtAuthenticationFilter.CURRENT_USER;
        Vendor vendor= vendorDao.findByUsername(currentUser).get();
        Driver driver = new Driver();

        driver.setDriver_name(driverModel.getDriverName());
        driver.setEmail(driverModel.getEmail());
        driver.setPassword(passwordEncoder.encode(driverModel.getPassword()));
        driver.setMobile_no(driverModel.getMobileNo());
        driver.setLicense_no(driverModel.getLicenseNo());
        driver.setAddress(driverModel.getAddress());
        driver.setAadhar_no(driverModel.getAadharNo());
        driver.setVerification_status(driverModel.getVerificationStatus());
        driver.setVendor(vendor);


        Role role = roleDao.findById("Driver")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        driver.setRole(userRoles);

        return driverDao.save(driver);

    }



    public List<Driver> getAlldriver() {

        return driverDao.findAll();
    }

    public Driver updateDriver(Long id, Driver driver) {
        Driver drivers =driverDao.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
        drivers.setDriver_name(driver.getDriver_name());
        drivers.setEmail(driver.getEmail());
        drivers.setPassword(driver.getPassword());
        drivers.setMobile_no(driver.getMobile_no());
        drivers.setLicense_no(driver.getLicense_no());
        drivers.setAadhar_no(driver.getAadhar_no());
        drivers.setDriver_img(driver.getDriver_img());
        drivers.setLicense_img(driver.getLicense_img());
        drivers.setId_proof_img(driver.getId_proof_img());
        drivers.setVerification_status(driver.getVerification_status());
        return driverDao.save(drivers);
    }
    public Optional<Driver> getDriverById(Long driverId) {
        return driverDao.findById(driverId);
    }
    public void deleteBus(Long id) {
        driverDao.deleteById(id);
    }


    public List<Driver> tobeVerifiedDriver() {

       return driverDao.findUnverifiedDrivers();
    }

    public Driver markVerifiedDriver(Long id) throws Exception {
        String currentUser = JwtAuthenticationFilter.CURRENT_USER;
        Vendor vendor= vendorDao.findByUsername(currentUser).get();
        Optional<Driver> optionalVendor = driverDao.findById(id);
        if (!optionalVendor.isPresent()) {
            throw new Exception("Vendor with ID " + id + " not found");
        }
        Driver dirver = optionalVendor.get();
        dirver.setVerification_status(true);
        dirver.setVendor(vendor);
        driverDao.save(dirver);
        return dirver;
    }

    public long getDriverCount() {
        return driverDao.count();
    }

    public List<Driver> getAllVerifiedDriver(DriverModel driverModel) {
        return driverDao.findByVerificationStatus(true);
    }

    public Driver addDriver(DriverModel driverModel) {
        Vendor vendor = vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        Driver driver = new Driver();

        driver.setDriver_name(driverModel.getDriverName());
        driver.setEmail(driverModel.getEmail());
        driver.setPassword(passwordEncoder.encode(driverModel.getPassword()));
        driver.setMobile_no(driverModel.getMobileNo());
        driver.setLicense_no(driverModel.getLicenseNo());
        driver.setAddress(driverModel.getAddress());
        driver.setAadhar_no(driverModel.getAadharNo());
        driver.setVerification_status(driverModel.getVerificationStatus());
        driver.setVendor(vendor);

        driver.setDriver_img(driverModel.getDriverImg());
        driver.setId_proof_img(driverModel.getIdProofImg());
        driver.setLicense_img(driverModel.getLicenseImg());

        Role role = roleDao.findById("Driver").orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        driver.setRole(userRoles);

        return driverDao.save(driver);

    }


    //

//    private final AmazonS3 s3Client;
//   // private final
//    private final String bucketName = "studycycle";
//  //  private final Driver driverRepository;
//
//    @Autowired
//    public DriverService(DriverDao driverDao) {
//        BasicAWSCredentials awsCreds = new BasicAWSCredentials("DO007AGUQ6TGMJKCUYJR", "+pxpyb8ittOzA+syz8pIZhSv5SjXS2j0aN6usFIX2gA");
//        this.s3Client = AmazonS3ClientBuilder.standard()
//                .withRegion("BLR1") // Replace with your region
//                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                .withPathStyleAccessEnabled(true)
//                .build();
//        this.driverDao = driverDao;
//    }
//
//    public void addDriver(DriverModel driverModel) throws IOException {
//        String currentUser = JwtAuthenticationFilter.CURRENT_USER;
//        Vendor vendor= vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
//        Driver driver = new Driver();
//
//
//        driver.setDriver_name(driverModel.getDriverName());
//        driver.setEmail(driverModel.getEmail());
//        driver.setPassword(passwordEncoder.encode(driverModel.getPassword()));
//        driver.setMobile_no(driverModel.getMobileNo());
//        driver.setLicense_no(driverModel.getLicenseNo());
//        driver.setAddress(driverModel.getAddress());
//        driver.setAadhar_no(driverModel.getAadharNo());
//        driver.setVerification_status(driverModel.getVerificationStatus());
//        driver.setVendor(vendor);
//
//        // Upload images to DigitalOcean Spaces
//        driver.setDriver_img(uploadImage(driverModel.getDriverImg()));
//        driver.setId_proof_img(uploadImage(driverModel.getIdProofImg()));
//        driver.setLicense_img(uploadImage(driverModel.getLicenseImg()));
//        Role role = roleDao.findById("Driver")
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//        Set<Role> userRoles = new HashSet<>();
//        userRoles.add(role);
//        driver.setRole(userRoles);
//        // Save driver information to the database
//        driverDao.save(driver);
//    }
//
//    private String uploadImage(MultipartFile file) throws IOException {
//        if (file == null || file.isEmpty()) {
//            return null;
//        }
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//        s3Client.putObject(bucketName, fileName, file.getInputStream(), null);
//        return s3Client.getUrl(bucketName, fileName).toString();
//    }
}

