package com.JustAlo.Service;

import com.JustAlo.Configuration.AmazonS3Config;
import com.JustAlo.Entity.*;
import com.JustAlo.Model.DriverModel;

import com.JustAlo.Repo.DriverDao;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.RouteRepository;
import com.JustAlo.Repo.TripRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DriverService {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TripRepository tripRepository;


    @Autowired
    private DriverDao driverDao;

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
        driver.setAddress(driverDTO.getAddress());
        driver.setAadhar_no(driverDTO.getAadharNo());
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
       for(Trip trip: trips){
           trip.getRoute().getOrigin();
           trip.getRoute().getDestination();
           trip.getDate();
           trip.getEndtime();
           trip.getTime();
       }
        return trips;


    }


    public List<Driver> getAllDriverByPerticularVendor(Long id) {
        return driverDao.findByVendorId(id);
    }
}
