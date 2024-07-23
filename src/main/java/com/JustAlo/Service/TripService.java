package com.JustAlo.Service;

import com.JustAlo.Entity.*;
import com.JustAlo.Model.LuxuryTripModel;
import com.JustAlo.Model.OrdinaryTripModel;
import com.JustAlo.Model.ScheduleTripModel;
import com.JustAlo.Repo.LuxuryTripRepository;
import com.JustAlo.Repo.OrdinaryTripRepository;
import com.JustAlo.Repo.ScheduledTripRepository;
import com.JustAlo.Repo.TripRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ScheduledTripRepository scheduledTripRepository;
    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    public Optional<Trip> findById(Long id) {
        return tripRepository.findById(id);
    }

    public Trip save(OrdinaryTripModel tripModel) {
        // Create and populate the Trip entity
        Trip trip = new Trip();
        trip.setType("Ordinary");
        trip.setBus(busService.getBusById(tripModel.bus_id).orElse(null));
        trip.setDriver(driverService.getDriverById(tripModel.driver_id).orElse(null));
        trip.setRoute(routeService.getRouteById(tripModel.route_id).orElse(null));
        trip.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));


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

    public LuxuryTrip save(LuxuryTripModel tripModel) {
        LuxuryTrip trip = new LuxuryTrip();
        trip.setType("Luxury");
        trip.setBus(busService.getBusById(tripModel.bus_id).orElse(null));
        trip.setDriver(driverService.getDriverById(tripModel.driver_id).orElse(null));
        trip.setRoute(routeService.getRouteById(tripModel.route_id).orElse(null));
        trip.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
        trip.setPickupPoints(tripModel.pickupPoints);
        trip.setDropDownPoints(tripModel.dropDownPoints);

        return luxuryTripRepository.save(trip);
    }
    public void deleteById(Long id) {
        tripRepository.deleteById(id);
    }

    public ScheduledTrip scheduleTrip(ScheduleTripModel trip) throws Exception {
        Trip trip1= tripRepository.findById(trip.trip_id).orElse(null);
        if(trip1!=null){
          return scheduledTripRepository.save(new ScheduledTrip(trip1,trip.date,trip.time));
        }
        throw new Exception("Trip not found");
    }
}
