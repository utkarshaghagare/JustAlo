package com.JustAlo.Controller;


import com.JustAlo.Entity.*;
import com.JustAlo.Repo.BusRepository;
import com.JustAlo.Repo.CityRepository;
import com.JustAlo.Repo.DriverDao;
import com.JustAlo.Repo.UserDao;
import com.JustAlo.Service.*;
import com.JustAlo.Entity.Admin;
import com.JustAlo.Entity.Route;
import com.JustAlo.Entity.User;
import com.JustAlo.Entity.Vendor;
import com.JustAlo.Service.AdminService;
import com.JustAlo.Service.RouteService;
import com.JustAlo.Service.UserService;
import com.JustAlo.Service.VendorService;
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

    @Autowired
    private CityRepository cityRepository;

    @Autowired
   private UserDao userDao;
    @Autowired
    private  DriverDao driverRepository;
    @Autowired
    private BusRepository busRepository;

    @Autowired
    private  RentService rentService;


    @PostMapping({"/registerAdmin"})
    public Admin registerNewAdmin(@RequestBody Admin admin) {
        return userService.registerAdmin(admin);
    }

    @GetMapping("/AllVendor")
    @PreAuthorize("hasRole('Admin')")
    public List<Vendor> getAllVendor(){
        return vendorService.getAllVendor();
    }

    @GetMapping("/getAllAdmin")
    @PreAuthorize("hasRole('Admin')")
    public List<Admin> getAllAdmin(){
        return adminService.getAllVendor();
    }

    @PostMapping("/blockUser/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<User> blockUser(@PathVariable("id") Long id) {
        User updatedUser = userService.blockUser(id);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/unblockUser/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<User> unblockUser(@PathVariable("id") Long id) {
        User updatedUser = userService.unblockUser(id);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

@PutMapping("/blockDriver/{id}")
@PreAuthorize("hasRole('Admin')")
public  ResponseEntity<Driver> blockDriver(@PathVariable ("id") Long id){
        Driver updateDriver =driverService.blockDriver(id);
        if(updateDriver != null){
            return ResponseEntity.ok(updateDriver);
        }else {
            return ResponseEntity.notFound().build();
        }
}

@PutMapping("/unblockDriver/{id}")
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<Driver> UnblockDriver(@PathVariable("id") Long id){
    Driver updateDriver =driverService.UnblockDriver(id);
    if(updateDriver != null){
        return ResponseEntity.ok(updateDriver);
    }else {
        return ResponseEntity.notFound().build();
    }

}

    @PostMapping("/addRoute")
    @PreAuthorize("hasRole('Admin','Vendor')")
    public ResponseEntity<Route> addRoute(@RequestBody Route route) {
        // Find the city by name (assuming the route contains city names for start and end points)
        City startCity = cityRepository.findByCityname(route.getOrigin());
        City endCity = cityRepository.findByCityname(route.getDestination());

        if (startCity == null || endCity == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Set the longitude and latitude in the Route entity
        route.setLongitute(startCity.getLongitude());
        route.setLatitute(startCity.getLatitude());
        route.setLongitute(endCity.getLongitude());
        route.setLatitute(endCity.getLatitude());

        // Save the route
        Route savedRoute = routeService.addRoute(route);
        return new ResponseEntity<>(savedRoute, HttpStatus.CREATED);
    }

//     @GetMapping("/getAllRoute")
//     @PreAuthorize("hasRole('Admin','Vendor')")
//     public List<Route> getAllRouts(){
//        return  routeService.getAllRouts();
//     }


    @GetMapping("/getAllRoutes")
 //   @PreAuthorize("hasRole('Admin','Vendor')")
    public List<Route> getallRouts(){
        return routeService.getAllRouts();
    }

    @GetMapping("/AllDriverByPerticularVendor/{id}")
    @PreAuthorize("hasRole('Admin')")
    public  List<Driver> getAllDriverByPerticularVendor(@PathVariable("id") Long id){
        return driverService.getAllDriverByPerticularVendor(id);

    }

    @GetMapping("/getAllBusByPerticularVendor/{id}")
    @PreAuthorize("hasAnyRole('Vendor', 'Admin')")
    public List<Bus> getAllBusByPerticularVendor(@PathVariable("id") Long id) {
        return busService.getAllBusByPerticularVendor(id);
    }

    @PostMapping("/addcity&bordingpoint")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<City> addCityAndBordingPoint(@RequestBody City city){
        City savedCity = cityRepository.save(city);
        return ResponseEntity.ok(savedCity);
    }
    @GetMapping("/getCityBy/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<City> getCityById(@PathVariable Long id) {
        return cityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/getAllCities")
    @PreAuthorize("hasAnyRole('Vendor', 'Admin')")
    public ResponseEntity<List<City>> getAllCities() {
        List<City> cities = cityRepository.findAll();
        return ResponseEntity.ok(cities);
    }

@GetMapping("/unverifieddriver")
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<List<Driver>> unverifiredDriverList() {
    List<Driver> drivers = driverRepository.findUnverifiedDrivers();
    return new ResponseEntity<>(drivers, HttpStatus.OK);
}

@PutMapping("/verifieddriver/{id}")
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<Driver>verifiredDriverList(@PathVariable("id") Long id) {
     return driverService.verifiredDriverList(id);
}
    @GetMapping("/unverifiedBus")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<Bus>> unverifiredBusList() {
        List<Bus> bus = busRepository.findUnverifiedBus();
        return new ResponseEntity<>(bus, HttpStatus.OK);
    }


    @PutMapping("/verifiedBus/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Bus> verifyBus(@PathVariable("id") Long id) {
       return   busService.verifyBus(id);

    }

    @GetMapping("/getRentdetails")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<Rent>> getRentdetails() {
        List<Rent> rents = rentService.getRentdetails();
        return ResponseEntity.ok(rents);
    }
}
