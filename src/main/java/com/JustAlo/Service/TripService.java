package com.JustAlo.Service;

import com.JustAlo.Entity.*;
import com.JustAlo.Model.LuxuryTripModel;
import com.JustAlo.Model.OrdinaryTripModel;
import com.JustAlo.Model.ScheduleTripModel;
import com.JustAlo.Repo.LuxuryTripRepository;
import com.JustAlo.Repo.OrdinaryTripRepository;
//import com.JustAlo.Repo.ScheduledTripRepository;
import com.JustAlo.Repo.TripRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BusService busService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private OrdinaryTripRepository ordinaryTripRepository;

    @Autowired
    private LuxuryTripRepository luxuryTripRepository;

    @Autowired
    private VendorService vendorService;

//    @Autowired
//    private ScheduledTripRepository scheduledTripRepository;
    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

//    public Optional<Trip> findById(Long id) {
//        return tripRepository.findById(id);
//    }

    public List<Trip> findById(Long id) {
        return tripRepository.findAllByVendor(vendorService.findById(id));
    }


    public Trip save(OrdinaryTripModel tripModel) throws Exception {
        // Create and populate the Trip entity
        Trip trip = new Trip();
      trip.setType("Ordinary");
        trip.setBus(busService.getVerfiedBusById(tripModel.bus_id));
        trip.setDriver(driverService.getVerifiedDriverById(tripModel.driver_id));
        trip.setRoute(routeService.getRouteById(tripModel.route_id).orElse(null));
        trip.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
        trip.setDate(tripModel.date);
        trip.setTime(tripModel.time);


        // Save the Trip entity to generate an ID
        Trip savedTrip = tripRepository.save(trip);

        // Create and save each OrdinaryTrip
        for (String stop : tripModel.stops) {
            OrdinaryTrip ordinaryTrip = new OrdinaryTrip();
            ordinaryTrip.setTrip(savedTrip);
            ordinaryTrip.setStops(stop);  // Assuming OrdinaryTrip has a field `stop` for each stop
            ordinaryTripRepository.save(ordinaryTrip);
        }

        return savedTrip;
    }

    public LuxuryTrip save(LuxuryTripModel tripModel) throws Exception {
        LuxuryTrip trip = new LuxuryTrip();
        trip.setType("Luxury");
        trip.setBus(busService.getVerfiedBusById(tripModel.bus_id));
        trip.setDriver(driverService.getVerifiedDriverById(tripModel.driver_id));
        trip.setRoute(routeService.getRouteById(tripModel.route_id).orElse(null));
        trip.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
        trip.setDate(tripModel.date);
        trip.setTime(tripModel.time);
        trip.setPickupPoints(tripModel.pickupPoints);
        trip.setDropDownPoints(tripModel.dropDownPoints);

        return luxuryTripRepository.save(trip);
    }

    public Trip scheduleTrip(ScheduleTripModel trip) throws Exception {
        Trip trip1= tripRepository.findById(trip.trip_id).orElse(null);
        if(trip1!=null){
            trip1.setTime(trip.time);
            trip1.setDate(trip.date);
          return tripRepository.save(trip1);
        }
        throw new Exception("Trip not found");
    }


    public Trip update(OrdinaryTripModel trip, Long trip_id) {
        Trip trip1= tripRepository.findById(trip_id).orElse(null);
        if(trip1!=null){
            trip1.setBus(busService.getBusById(trip.bus_id).orElse(null));
            trip1.setDriver(driverService.getDriverById(trip.driver_id).orElse(null));
            trip1.setRoute(routeService.getRouteById(trip.route_id).orElse(null));
            trip1.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
            Trip savedTrip = tripRepository.save(trip1);

            ordinaryTripRepository.deleteAllByTrip(trip1);

            // Create and save each OrdinaryTrip
            for (String stop : trip.stops) {
                OrdinaryTrip ordinaryTrip = new OrdinaryTrip();
                ordinaryTrip.setTrip(savedTrip);
                ordinaryTrip.setStops(stop);  // Assuming OrdinaryTrip has a field `stop` for each stop
                ordinaryTripRepository.save(ordinaryTrip);
            }
            return trip1;
        }
        return null;
    }


    public LuxuryTrip update(LuxuryTripModel tripModel,long trip_id) {
        LuxuryTrip trip = luxuryTripRepository.findById(trip_id).orElse(null);
        //trip.setType("Luxury");
        if(trip!=null){
            trip.setBus(busService.getBusById(tripModel.bus_id).orElse(null));
            trip.setDriver(driverService.getDriverById(tripModel.driver_id).orElse(null));
            trip.setRoute(routeService.getRouteById(tripModel.route_id).orElse(null));
            trip.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
            //check these
            trip.setPickupPoints(tripModel.pickupPoints);
            trip.setDropDownPoints(tripModel.dropDownPoints);
            return luxuryTripRepository.save(trip);
        }
       return null;
    }

    public void deleteById(Long id) {
        ordinaryTripRepository.deleteAllByTrip(tripRepository.findById(id).orElse(null));
        //luxuryTripRepository.delete
        tripRepository.deleteById(id);
        //check if Luxury spots are deleted aswell
    }

}
