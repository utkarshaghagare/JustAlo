package com.JustAlo.Controller;


import com.JustAlo.Entity.*;
import com.JustAlo.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {

    @Autowired
   private UserService userService;
    @Autowired
    private VendorService vendorService;
    @Autowired
    private AdminService adminService;

    @Autowired
    private RouteService routeService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private  BusService busService;


    @PostMapping({"/registerAdmin"})
    public Admin registerNewAdmin(@RequestBody Admin admin) {
        return userService.registerAdmin(admin);
    }

    @GetMapping("/AllVendor")
    @PreAuthorize("hasRole('Super Admin')")
    public List<Vendor> getAllVendor(){
        return vendorService.getAllVendor();
    }

    @GetMapping("/getAllAdmin")
    public List<Admin> getAllAdmin(){
        return adminService.getAllVendor();
    }

    @PostMapping("/blockUser/{id}")
    public ResponseEntity<User> blockUser(@PathVariable("id") Long id) {
        User updatedUser = userService.blockUser(id);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/unblockUser/{id}")
    public ResponseEntity<User> unblockUser(@PathVariable("id") Long id) {
        User updatedUser = userService.unblockUser(id);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/addRoute")
    public ResponseEntity<Route> addRoute(@RequestBody Route route) {
        Route savedRoute = routeService.addRoute(route);
        return new ResponseEntity<>(savedRoute, HttpStatus.CREATED);
    }


    @GetMapping("/AllDriverByPerticularVendor/{id}")
    public  List<Driver> getAllDriverByPerticularVendor(@PathVariable("id") Long id){
        return driverService.getAllDriverByPerticularVendor(id);

    }

    @GetMapping("/getAllBusByPerticularVendor/{id}")
    public List<Bus> getAllBusByPerticularVendor(@PathVariable("id") Long id) {
        return busService.getAllBusByPerticularVendor(id);
    }

}
