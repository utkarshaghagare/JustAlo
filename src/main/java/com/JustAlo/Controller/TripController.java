package com.JustAlo.Controller;

import com.JustAlo.Entity.LuxuryTrip;
import com.JustAlo.Entity.ScheduledTrip;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Model.LuxuryTripModel;
import com.JustAlo.Model.OrdinaryTripModel;
import com.JustAlo.Model.ScheduleTripModel;
import com.JustAlo.Service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/trips")
public class TripController {
    @Autowired
    private TripService tripService;

//    @GetMapping
//    public List<Trip> getAllTrips() {
//        return tripService.findAll();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
//        Optional<Trip> trip = tripService.findById(id);
//        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

    @PostMapping("/ordinary")
    @PreAuthorize("hasRole('Vendor')")
    public Trip createTrip(@RequestBody OrdinaryTripModel trip) {
        return tripService.save(trip);
    }

    @PostMapping("/luxury")
    @PreAuthorize("hasRole('Vendor')")
    public LuxuryTrip createTrip(@RequestBody LuxuryTripModel trip) {
        return tripService.save(trip);
    }

    @PostMapping
    @PreAuthorize("hasRole('Vendor')")
    public ScheduledTrip ScheduleTrip(@RequestBody ScheduleTripModel trip) throws Exception {
        return tripService.scheduleTrip(trip);
    }

    @PostMapping
    @PreAuthorize("hasRole('Vendor')")
    public ScheduledTrip updateScheduledTrip(@RequestBody ScheduleTripModel trip) throws Exception {
        return tripService.scheduleTrip(trip);
    }
//    @PostMapping
//    @PreAuthorize("hasRole('Vendor')")
//    public ScheduledTrip updateTrip(@RequestBody ScheduleTripModel trip) throws Exception {
//        return tripService.scheduleTrip(trip);
//    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Trip> updateTrip(@PathVariable Long id, @RequestBody Trip tripDetails) {
//        Optional<Trip> trip = tripService.findById(id);
//        if (trip.isPresent()) {
//            Trip updatedTrip = trip.get();
//            updatedTrip.setBusId(tripDetails.getBusId());
//            updatedTrip.setDriverId(tripDetails.getDriverId());
//            updatedTrip.setRouteId(tripDetails.getRouteId());
//            updatedTrip.setType(tripDetails.getType());
//            updatedTrip.setVendorId(tripDetails.getVendorId());
//            return ResponseEntity.ok(tripService.save(updatedTrip));
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
