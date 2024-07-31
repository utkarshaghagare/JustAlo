package com.JustAlo.Controller;


import com.JustAlo.Entity.Vendor;
import com.JustAlo.Model.VendorModel;
import com.JustAlo.Service.BusService;
import com.JustAlo.Service.DriverService;
import com.JustAlo.Service.UserService;
import com.JustAlo.Service.VendorService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class VendorController {
    @Autowired
    private DriverService driverService;
    @Autowired
    private BusService busService;

    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @PostConstruct
    public void initRoleAndVendor() {
    	userService.initRoleAndUser();
    }

    @PostMapping({"/registerNewVendor"})
    public Vendor registerNewUser(@RequestBody VendorModel vendormodel) {
        return vendorService.registerVendor(vendormodel);
    }

    @GetMapping({"/tobeVerified"})
    public List<Vendor> tobeVerified(){
        return vendorService.tobeVerified();
    }

    @PreAuthorize("hasRole('Vendor')")
    @PostMapping("/markVerified/{id}")
    public Vendor markVerified(@PathVariable("id") Long id) throws Exception {
        return vendorService.markVerified(id);
    }

    @GetMapping("/getDriverCount")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Long> getDriverCount() {
        long driverCount = driverService.getDriverCount();
        return ResponseEntity.ok(driverCount);
    }

    @GetMapping("/getBusesCount")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Long> getAllBuses(){
        long busCount= busService.getBusesCount();
        return ResponseEntity.ok(busCount);
    }
}
