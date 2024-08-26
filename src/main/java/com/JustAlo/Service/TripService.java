package com.JustAlo.Service;

import com.JustAlo.Entity.*;
import com.JustAlo.Model.*;
import com.JustAlo.Repo.*;
//import com.JustAlo.Repo.ScheduledTripRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.JustAlo.Model.BusStatus.COMPLETED;
import static com.JustAlo.Model.BusStatus.RUNNING;

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
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserDao userDao;

    @Autowired
    private PointsRepository pointsRepository;

    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    public List<Trip> findByVendorId(Long id) {
        return tripRepository.findAllByVendor(vendorService.findById(id));
    }

    public Trip findById(Long id){
        return tripRepository.findById(id).orElse(null);
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
        trip.setEndtime(tripModel.endtime);

        // Save the Trip entity to generate an ID
        Trip savedTrip = tripRepository.save(trip);
//check
        makeOrdinaryTrip(tripModel,savedTrip);

        makeBooking(savedTrip.getBus().getTotal_seats(),savedTrip);

        return savedTrip;
    }

    public void makeOrdinaryTrip(OrdinaryTripModel trip, Trip savedTrip){
        int i=1;
        // Create and save each OrdinaryTrip
        for (Stop stop : trip.stops) {
            OrdinaryTrip ordinaryTrip = new OrdinaryTrip();
            ordinaryTrip.setTrip(savedTrip);
            ordinaryTrip.setStopname(stop.Stop_name);// Assuming OrdinaryTrip has a field `stop` for each stop
            ordinaryTrip.setAmount(stop.amount);
//            ordinaryTrip.setLatitute(stop.latitute);
//            ordinaryTrip.setLongitute(stop.longitute);
            List<Points> points= pointsRepository.saveAll(stop.points);
            ordinaryTrip.setPoints(stop.points);
            ordinaryTrip.setStopnumber(i++);  // Assuming OrdinaryTrip has a field `stop` for each stop

            ordinaryTripRepository.save(ordinaryTrip);
        }
    }
    public void makeBooking(int n, Trip savedTrip){
        List<Booking> bookings = new ArrayList<>();
        List<String> allStops = ordinaryTripRepository.findAllStopNames(savedTrip);
        for(int j=1; j<=n; j++){
            bookings.add(new Booking(savedTrip,j,allStops));
        }
        bookingRepository.saveAll(bookings);
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
        trip.setEndtime(tripModel.endtime);
        trip.setAmount(tripModel.amount);
        trip.setPickupPoints(tripModel.pickupPoints);
        trip.setDropDownPoints(tripModel.dropDownPoints);
//check
        LuxuryTrip savedTrip=luxuryTripRepository.save(trip);
        makeBooking(savedTrip.getBus().getTotal_seats(),savedTrip);
        return savedTrip;
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
            makeOrdinaryTrip(trip,savedTrip);
            //it doesnt need to b updated
            //makeBooking(savedTrip.getBus().getTotal_seats(),savedTrip);
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
            trip.setAmount(tripModel.amount);
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

//check
    public List<Trip> findTrip(String start, String destination, Date date) {
        List<Trip> TripsByDate = tripRepository.findAllByDate(date);
        List<Trip> Trips = new ArrayList<>(); // Initialize as an empty list
        if (TripsByDate != null) { // Check if TripsByDate is not null
            for (Trip t : TripsByDate) {
                Route route = t.getRoute();
                if (route != null && route.getOrigin().equals(start) && route.getDestination().equals(destination)) {
                    Trips.add(t);
                }
            }
        }
        return Trips;
    }

    public Seats findSeat(String start, String destination,Long trip_id) {
        Trip trip= findById(trip_id);
        if(trip!=null){
          return bookingService.findSeats(start,destination,trip);
        }
        return null;
    }



    public String bookSeat(TicketBooking ticketBooking) throws Exception {
        Trip trip= findById(ticketBooking.getTrip_id());
        if(trip!=null){
            return bookingService.bookSeat(ticketBooking,trip);
        }
        return null;
    }

    public List<Booking> getTickets(String status) {
        return bookingRepository.findAllByPassenger_User_Id(userDao.findByEmail(JwtAuthenticationFilter.CURRENT_USER),status);
    }

    public void cancelTicket(Long id) {
        bookingService.cancelTicket(id);
    }

    public List<Passenger> getPassengers() {
        return bookingService.getPassengers(userDao.findByEmail(JwtAuthenticationFilter.CURRENT_USER));
    }

    public List<JourneyDetails> getdetails(long id) {

        Trip trip =tripRepository.findById(id).orElse(null);
        if(trip==null){
         return null;

            }
        List<JourneyDetails> journeyDetailsList=new ArrayList<>();
       List<OrdinaryTrip> stop = ordinaryTripRepository.findAllByTripId(trip.getId());
        int rem=0;
        for (OrdinaryTrip o:stop) {
            JourneyDetails j= bookingService.getdetails(trip,o.getStopname(),rem);
            rem= j.getRemaining();
            journeyDetailsList.add(j);
        }
        return journeyDetailsList;
    }

    public Trip startTrip(long id) {
        // Fetch the trip by ID, handle the case where the trip is not found
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        // Check the current status and update accordingly
        if (trip.getStatus() == null) {
            trip.setStatus("RUNNING");
        } else if (trip.getStatus().equals("RUNNING")) {
            trip.setStatus("COMPLETED");
            busService.turnOffBus(trip.getBus().getId(),BusStatus.AVAILABLE);
            driverService.UnblockDriver(trip.getDriver().getId());
        }

        // Save the updated trip back to the repository
        return tripRepository.save(trip);
    }
    public List<RecentBookingsRoute> findRecentTrips() {
        // Retrieve all bookings sorted by date in descending order
        List<Booking> recentBookings = bookingRepository.findByPassengerOrderByBookingDateDesc(
                userDao.findByEmail(JwtAuthenticationFilter.CURRENT_USER)
        );

        List<RecentBookingsRoute> response = new ArrayList<>(5);
        Set<String> uniqueRoutes = new HashSet<>();

        for (Booking b : recentBookings) {
            Route r = b.getTrip().getRoute();
            String routeKey = r.getOrigin() + "-" + r.getDestination(); // Create a unique key for the route

            // Add the route to the response only if it's unique
            if (!uniqueRoutes.contains(routeKey)) {
                RecentBookingsRoute record = new RecentBookingsRoute(r.getOrigin(), r.getDestination());
                response.add(record);
                uniqueRoutes.add(routeKey);
            }

            // Stop the loop if we've collected 5 unique routes
            if (response.size() == 5) {
                break;
            }
        }

        return response;
    }

    public List<Trip> findTripsFromOrigin(String longitude, String latitude) {
        // Retrieve all trips with a date of today or later
        Date today = Date.valueOf(LocalDate.now());
        List<Trip> allTrips = tripRepository.findTripsFromToday(today);

        List<Trip> response = new ArrayList<>(5);

        for (Trip trip : allTrips) {
            // Compare the trip's origin with the user's current location (longitude and latitude)
            Route route = trip.getRoute();
            if (route.getLongitute().equals(longitude) && route.getLatitute().equals(latitude)) {
                response.add(trip);
            }

            // Stop the loop if we have 5 trips in the response
            if (response.size() == 5) {
                break;
            }
        }

        return response;
    }

}

