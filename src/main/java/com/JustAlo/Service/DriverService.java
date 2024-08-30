package com.JustAlo.Service;

import com.JustAlo.Configuration.AmazonS3Config;
import com.JustAlo.Entity.*;
import com.JustAlo.Model.DriverModel;

import com.JustAlo.Model.enums.DriverStatus;
import com.JustAlo.Repo.DriverDao;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.RouteRepository;
import com.JustAlo.Repo.TripRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriverService {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TripRepository tripRepository;


    @Autowired
    private DriverDao driverDao;
    @Autowired
    private VendorService vendorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AmazonS3Config s3Service;
    @Autowired
    private RoleDao roleDao;

    public List<Driver> getAllDriver() {
        return driverDao.findAll();
    }


    public Driver addDriver(DriverModel driverDTO) {
        Driver driver = new Driver();

        driver.setDriver_name(driverDTO.getDriverName());
        driver.setEmail(driverDTO.getEmail());
        driver.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
        driver.setMobile_no(driverDTO.getMobileNo());
        driver.setLicense_no(driverDTO.getLicenseNo());
        driver.setStatus(DriverStatus.ACTIVE);
        driver.setAddress(driverDTO.getAddress());
        driver.setAadhar_no(driverDTO.getAadharNo());
       // driver.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
        driver.setVerification_status(driverDTO.getVerificationStatus());
       ;

        Role role = roleDao.findById("Driver")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        driver.setRole(userRoles);

        return driverDao.save(driver);
    }

    public Optional<Driver> getDriverById(Long driverId) {
        return driverDao.findById(driverId);
    }

    public List<Driver> getAllVerifiedDriver(DriverModel driverModel) {
        return driverDao.findByVerificationStatus(true);
    }

    public Driver getVerifiedDriverById(Long driverId) throws Exception {

        Driver driver=driverDao.findById(driverId).orElse(null);
        if(driver!=null){
            if(driverDao.isDriverVerified(driverId)){
                driver.setStatus(DriverStatus.UNAVAILABLE);
                driverDao.save(driver);
                return driver ;
            }
            else{
                throw new Exception("Driver Unverified");
            }
        }
        throw new Exception("Driver not found");
    }


    public List<Trip> getDriverTripDetails() {

        String email = JwtAuthenticationFilter.CURRENT_USER;

        // Find the driver by the current user ID
        Driver driver = driverDao.findByEmail(email).get();

        // Fetch trips associated with the current driver
        List<Trip> trips = tripRepository.findByDriverId(driver.getId());

        LocalDate today = LocalDate.now();
        LocalDate upcomingDate = today.plusDays(5); // Next 5 days
        List<Trip> upcomingTrips = trips.stream()
                .filter(trip -> !trip.getDate().isBefore(today) && !trip.getDate().isAfter(upcomingDate)) // Within today and next 5 days
                .collect(Collectors.toList());
       for(Trip trip: upcomingTrips){
           trip.getRoute().getOrigin();
           trip.getRoute().getDestination();
           trip.getDate();
           trip.getEndtime();
           trip.getTime();
       }
        return upcomingTrips;


    }


    public List<Driver> getAllDriverByPerticularVendor(Long id) {
        return driverDao.findByVendorId(id);
    }

    public Driver blockDriver(Long id) {
        Optional<Driver> optionaldriver = driverDao.findById(id);
        if(optionaldriver.isPresent()){
            Driver driver=optionaldriver.get();
            driver.setStatus(DriverStatus.BLOCKED);
            driver.setVerification_status(false);
            driverDao.save(driver);
            return driver;
        }else {
            return  null;
        }
    }

    public Driver UnblockDriver(Long id) {
        Optional<Driver> optionalDriver = driverDao.findById(id);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get(); // Retrieve the existing driver
            driver.setStatus(DriverStatus.ACTIVE); // Update the status to ACTIVE
            driver.setVerification_status(true); // Set verification status to true
            return driverDao.save(driver); // Save the updated driver entity
        } else {
            return null;
        }
    }

    public List<Driver> getAllVerifiedAvailableDrivers() {
        List<Driver> drivers= getAllDriverByPerticularVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER).getId());
    List<Driver> response= new ArrayList<>();
        for(Driver d:drivers){
        if(d.getVerification_status() && d.getStatus().equals(DriverStatus.ACTIVE)){
            response.add(d);
        }
    }
        return response;
    }

    public ResponseEntity<Driver> verifiredDriverList(Long id) {
        Optional<Driver> optionalDriver = driverDao.findById(id);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setVerification_status(true);
            driverDao.save(driver);
            return new ResponseEntity<>(driver, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    public ResponseEntity<Driver> getunverifiredDriverList() {
//        return driverDao.unverifiredDriverList();
//    }
}
