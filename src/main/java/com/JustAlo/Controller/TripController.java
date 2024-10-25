package com.JustAlo.Controller;

import com.JustAlo.Entity.LuxuryTrip;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Entity.Vendor;
import com.JustAlo.Model.LuxuryTripModel;
import com.JustAlo.Model.OrdinaryTripModel;
import com.JustAlo.Model.ScheduleTripModel;
import com.JustAlo.Security.JwtAuthenticationFilter;
import com.JustAlo.Service.TripService;
import com.JustAlo.Service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/trips")
public class TripController {
    @Autowired
    private TripService tripService;
    @Autowired
    public VendorService vendorService;

    @GetMapping("/TripById/{vendorid}")
    @PreAuthorize("hasRole('Vendor')")
    public List<Trip> getTripById(@PathVariable Long vendorid) {
        return tripService.findByVendorId(vendorid);
    }


    @PostMapping("/ordinary")
    @PreAuthorize("hasRole('Vendor')")
    public Trip createTrip(@RequestBody OrdinaryTripModel trip) throws Exception {
        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(vendor!=null && vendor.getVerification_status()) {
            return tripService.save(trip);
        }
        else throw new Exception("Vendor Unverified bY Admin");
    }

    @PostMapping("/luxury")
    @PreAuthorize("hasRole('Vendor')")
    public LuxuryTrip createTrip(@RequestBody LuxuryTripModel trip) throws Exception {
        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(vendor!=null && vendor.getVerification_status()) {
            return tripService.save(trip);
        }
        else throw new Exception("Vendor Unverified bY Admin");
    }


    //Scheduling if not already
    @PostMapping("/ScheduleTrip")
    @PreAuthorize("hasRole('Vendor')")
    public Trip ScheduleTrip(@RequestBody ScheduleTripModel trip) throws Exception {

        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(vendor!=null && vendor.getVerification_status()) {
            return tripService.scheduleTrip(trip);
        }
        else throw new Exception("Vendor Unverified bY Admin");
    }

    @PutMapping("/update/ordinary/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public Trip updateTrip(@RequestBody OrdinaryTripModel trip,@PathVariable long id) throws Exception {
        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(vendor!=null && vendor.getVerification_status()) {
            return tripService.update(trip,id);
        }
        else throw new Exception("Vendor Unverified bY Admin");

    }

    @PutMapping("/update/Luxury/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public LuxuryTrip updateTrip(@RequestBody LuxuryTripModel trip,@PathVariable long id) throws Exception {
        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(vendor!=null && vendor.getVerification_status()) {
            return tripService.update(trip,id);
        }
        else throw new Exception("Vendor Unverified bY Admin");

    }


    //RESCHEDULING -Scheduling Again
    @PutMapping("/reschedule/ordinary")
    @PreAuthorize("hasRole('Vendor')")
    public Trip reScheduleTrip(@RequestBody OrdinaryTripModel trip) throws Exception {

        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(vendor!=null && vendor.getVerification_status()) {
            return tripService.save(trip);
        }
        else throw new Exception("Vendor Unverified bY Admin");
    }

    @PutMapping("/reschedule/Luxury")
    @PreAuthorize("hasRole('Vendor')")
    public LuxuryTrip reScheduleTrip(@RequestBody LuxuryTripModel trip) throws Exception {
        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(vendor!=null && vendor.getVerification_status()) {
            return tripService.save(trip);
        }
        else throw new Exception("Vendor Unverified bY Admin");
    }


//DELETING
    @DeleteMapping("deleteTrip/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) throws Exception {
        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(vendor!=null && vendor.getVerification_status()) {
            tripService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else throw new Exception("Vendor Unverified bY Admin");

    }
}
