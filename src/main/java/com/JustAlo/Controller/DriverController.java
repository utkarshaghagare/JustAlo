package com.JustAlo.Controller;

import com.JustAlo.Configuration.AmazonS3Config;
import com.JustAlo.Entity.Driver;
import com.JustAlo.Entity.Role;
import com.JustAlo.Entity.Trip;
//import com.JustAlo.Model.AlertRequest;
import com.JustAlo.Model.DriverModel;

import com.JustAlo.Model.JourneyDetails;
import com.JustAlo.Model.TripDTO;
import com.JustAlo.Model.enums.DriverStatus;
import com.JustAlo.Repo.DriverDao;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Security.JwtAuthenticationFilter;
//import com.JustAlo.Service.AlertService;
import com.JustAlo.Service.DriverService;
import com.JustAlo.Service.TripService;
import com.JustAlo.Service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class DriverController {
    private final S3Client s3Client;
    private final String bucketName = "studycycle";
    private final String spaceUrl = "https://studycycle.blr1.digitaloceanspaces.com/";
    @Autowired
    public DriverController(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverDao driverDao;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private TripService tripService;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private AmazonS3Config amazonS3Config;

    @Autowired
    private VendorService vendorService;

//    @Autowired
//    private AlertService alertService;

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }


    @PostMapping("/addDriver")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Driver> addDriver(@RequestParam("driverName") String driverName,
                                            @RequestParam("driverNickname") String driverNickname,
                                            @RequestParam("email") String email,
                                            @RequestParam("password") String password,
                                            @RequestParam("mobileNo") String mobileNo,
                                            @RequestParam("licenseNo") String licenseNo,
                                            @RequestParam("address") String address,
                                            @RequestParam("aadharNo") String aadharNo,
                                            @RequestParam("driver_img") MultipartFile driverImg,
                                            @RequestParam("id_proof_img") MultipartFile idProofImg,
                                            @RequestParam("license_img") MultipartFile licenseImg
                                           ) {

        try {
            // Upload images to DigitalOcean Spaces
            String driverImgUrl = uploadFileToSpace(driverImg);
            String idProofImgUrl = uploadFileToSpace(idProofImg);
            String licenseImgUrl = uploadFileToSpace(licenseImg);

            // Save Driver with image URLs
            Driver driver = new Driver();
            driver.setDriver_name(driverName);
            driver.setDriver_nickname(driverNickname);
            driver.setEmail(email);
            driver.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
            driver.setPassword(getEncodedPassword(password));
         //   vendor.setPassword(getEncodedPassword(vendormodel.getPassword()));
            driver.setMobile_no(mobileNo);
            driver.setLicense_no(licenseNo);
            driver.setAddress(address);
            driver.setAadhar_no(aadharNo);
            driver.setDriver_img(driverImgUrl);
            driver.setId_proof_img(idProofImgUrl);
            driver.setLicense_img(licenseImgUrl);
            driver.setVerification_status(false);
            driver.setStatus(DriverStatus.ACTIVE);

            Role role = roleDao.findById("Driver")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(role);
            driver.setRole(userRoles);

            Driver savedDriver = driverDao.save(driver);

            return ResponseEntity.ok(savedDriver);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    private String uploadFileToSpace(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + StringUtils.cleanPath(file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Path tempFile = Files.createTempFile("upload-", fileName);

        try (var inputStream = file.getInputStream()) {
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
    @GetMapping("/getAllDriver")
    @PreAuthorize("hasRole('Vendor')")
    public List<Driver> getAllDriver(){
        return driverService.getAllDriver();

    }

    @GetMapping("/verifiedDrivers")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<List<Driver>> getAllVerifiedDriver(@RequestBody DriverModel driverModel){
        List<Driver> drivers= driverService.getAllVerifiedDriver(driverModel);
        return ResponseEntity.ok(drivers);
    }
    @GetMapping("/getdetails/{id}")
    @PreAuthorize("hasRole('Driver')")
    public List<JourneyDetails> getdetails(@PathVariable("id") long id ) throws Exception {

        return tripService.getdetails(id);
    }

    @GetMapping("/getDriverTripDetails")
    @PreAuthorize("hasRole('Driver')")
    public ResponseEntity<List<TripDTO>>  getDriverTripDetails(){
        List<TripDTO>d=  driverService.getDriverTripDetails();
        return ResponseEntity.ok(d);
    }

    @PutMapping("/startTrip/{id}")
    @PreAuthorize("hasRole('Driver')")
    public ResponseEntity<Trip> startTrip(
            @PathVariable("id") long id,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {

        Trip updatedTrip = tripService.startTrip(id, latitude, longitude);
        return ResponseEntity.ok(updatedTrip);
    }

//    @PostMapping("/sendAlert")
//    @PreAuthorize("hasRole('Driver')")
//    public ResponseEntity<String> sendAlert(@RequestBody AlertRequest alertRequest) {
//        alertService.sendAlertToVendor(alertRequest);
//        return new ResponseEntity<>("Alert sent to vendor successfully", HttpStatus.OK);
//    }
}
