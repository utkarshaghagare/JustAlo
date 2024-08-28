package com.JustAlo.Controller;

import com.JustAlo.Entity.LuxuryTrip;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Model.LuxuryTripModel;
import com.JustAlo.Model.OrdinaryTripModel;
import com.JustAlo.Model.ScheduleTripModel;
import com.JustAlo.Service.TripService;
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

//Should we get trip as ordinary and luxury differently
    @GetMapping("/TripById/{vendorid}")
    @PreAuthorize("hasRole('Vendor')")
    public List<Trip> getTripById(@PathVariable Long vendorid) {
        return tripService.findByVendorId(vendorid);
        // trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }



    //Creating Trip
    @PostMapping("/ordinary")
    @PreAuthorize("hasRole('Vendor')")
    public Trip createTrip(@RequestBody OrdinaryTripModel trip) throws Exception {
        return tripService.save(trip);
    }

    @PostMapping("/luxury")
    @PreAuthorize("hasRole('Vendor')")
    public LuxuryTrip createTrip(@RequestBody LuxuryTripModel trip) throws Exception {
        return tripService.save(trip);
    }


    //Scheduling if not already
    @PostMapping("/ScheduleTrip")
    @PreAuthorize("hasRole('Vendor')")
    public Trip ScheduleTrip(@RequestBody ScheduleTripModel trip) throws Exception {
        return tripService.scheduleTrip(trip);
    }


    //Updating Already Scheduled
    @PutMapping("/update/ordinary/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public Trip updateTrip(@RequestBody OrdinaryTripModel trip,@PathVariable long id) {
        return tripService.update(trip,id);
    }

    @PutMapping("/update/Luxury/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public LuxuryTrip updateTrip(@RequestBody LuxuryTripModel trip,@PathVariable long id) {
        return tripService.update(trip,id);
    }


    //RESCHEDULING -Scheduling Again
    @PutMapping("/reschedule/ordinary")
    @PreAuthorize("hasRole('Vendor')")
    public Trip reScheduleTrip(@RequestBody OrdinaryTripModel trip) throws Exception {
        return tripService.save(trip);
    }

    @PutMapping("/reschedule/Luxury")
    @PreAuthorize("hasRole('Vendor')")
    public LuxuryTrip reScheduleTrip(@RequestBody LuxuryTripModel trip) throws Exception {
        return tripService.save(trip);
    }


//DELETING
    @DeleteMapping("deleteTrip/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
