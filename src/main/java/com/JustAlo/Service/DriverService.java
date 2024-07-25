package com.JustAlo.Service;

import com.JustAlo.Configuration.AmazonS3Config;
import com.JustAlo.Entity.Driver;
import com.JustAlo.Entity.Role;
import com.JustAlo.Entity.User;
import com.JustAlo.Model.DriverModel;
import com.JustAlo.Repo.DriverDao;
import com.JustAlo.Repo.RoleDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DriverService {


    @Autowired
    private DriverDao driverDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AmazonS3Config s3Service;
    @Autowired
    private RoleDao roleDao;

    public List<Driver> getAllDriver(DriverModel driverModel) {
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
}
