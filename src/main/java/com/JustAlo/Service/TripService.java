package com.JustAlo.Service;

import com.JustAlo.Entity.*;
import com.JustAlo.Entity.Points;
import com.JustAlo.Model.*;
import com.JustAlo.Model.enums.DriverStatus;
import com.JustAlo.Repo.*;
//import com.JustAlo.Repo.ScheduledTripRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
    private VendorDao vendorDao;
    @Autowired
    private PointsRepository pointsRepository;
    @Autowired
    private DriverDao driverDao;

    @Autowired
    private CostingRepository costingRepository;

    @Autowired
    private StopPriceRepository stopPriceRepository;

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
        StopPrice trip = new StopPrice();
        trip.setType("Ordinary");
        trip.setBus(busService.getVerfiedBusById(tripModel.bus_id));
        trip.setDriver(driverService.getVerifiedDriverById(tripModel.driver_id));
        trip.setRoute(routeService.getRouteById(tripModel.route_id).orElse(null));
        trip.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
        trip.setDate(tripModel.date);
        trip.setTime(tripModel.time);
        trip.setEndtime(tripModel.endtime);
      //  costingRepository.saveAll(tripModel.stop_price);
        trip.setPrices(costingRepository.saveAll(tripModel.stop_price));

        // Save the Trip entity to generate an ID
        Trip savedTrip = stopPriceRepository.save(trip);
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
        pointsRepository.saveAll(tripModel.pickupPoints);
        pointsRepository.saveAll(tripModel.dropDownPoints);
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

    public void deleteById(Long id) throws Exception {
        Trip trip = tripRepository.findById(id).orElse(null);

        if (trip != null) {
            List<Booking> bookings= bookingRepository.findAllByTrip(trip);
            for(Booking b: bookings){
                if("BOOKED".equals(b.getStatus())){
                    throw new
                            Exception("Booked Seats for trips!");
                }
                bookingRepository.deleteById(b.getId());
            }
            // Delete Points associated with OrdinaryTrip first
            List<OrdinaryTrip> ordinaryTrips = ordinaryTripRepository.findAllByTrip(trip);
            for (OrdinaryTrip ordinaryTrip : ordinaryTrips) {
                pointsRepository.deleteAll(ordinaryTrip.getPoints());
            }

            // Delete OrdinaryTrips
            ordinaryTripRepository.deleteAll(ordinaryTrips);

            // Proceed to delete Trip
            tripRepository.deleteById(id);
        }
    }

//check
public List<Trip> findTrip(String start, String destination, Date date) {
    LocalDate localDate = date.toLocalDate();

    List<Trip> TripsByDate = tripRepository.findAllByDate(date.toLocalDate());
  // List<Trip> t= tripRepository.f

    List<Trip> Trips = new ArrayList<>(); // Initialize as an empty list
    if (TripsByDate != null) { // Check if TripsByDate is not null
        for (Trip t : TripsByDate) {
            Route route = t.getRoute();
            if (route != null && route.getOrigin().equals(start) && route.getDestination().equals(destination)) {
                if(date.after(Date.valueOf(LocalDate.now()))){
                    Trips.add(t);
                }
                else if(t.getTime().toLocalTime().isAfter(LocalTime.now().plusMinutes(15))){
                    Trips.add(t);
                }

            }
            else if(t.getType().equals("Ordinary")){
                List<OrdinaryTrip> stops= ordinaryTripRepository.findAllByTripId(t.getId());
                List<String> trip_stops= new ArrayList<>();
                for(OrdinaryTrip o: stops){
                    trip_stops.add(o.getStopname());
                }
                //check if trip_stops contain start and destination
                int startIndex = trip_stops.indexOf(start);
                int destinationIndex = trip_stops.indexOf(destination);

                if (startIndex != -1 && destinationIndex != -1 && startIndex < destinationIndex ) {
                    if(date.after(Date.valueOf(LocalDate.now()))){
                        Trips.add(t);
                    }
                    else if(t.getTime().toLocalTime().isAfter(LocalTime.now().plusMinutes(15))){
                        Trips.add(t);
                    }
                    // Add trip to list if both stops exist and start comes before destination
                  //  Trips.add(t);
                }
            }
        }
    }
    return Trips;
}


//Ankit find trip code

//    public List<Trip> findTrip(String start, String destination, Date date) {
//        LocalDate localDate = date.toLocalDate();
//        System.out.println("Searching for trips on date: " + date);
//
//        List<Trip> tripsByDate = tripRepository.findAllByDate(localDate);
//        System.out.println("Found trips by date: " + tripsByDate);
//
//        List<Trip> trips = new ArrayList<>();
//
//        if (tripsByDate != null) {
//            for (Trip t : tripsByDate) {
//                Route route = t.getRoute();
//                if (route != null &&
//                        route.getOrigin().equals(start) &&
//                        route.getDestination().equals(destination) &&
//                        t.getTime().toLocalTime().isAfter(LocalTime.now().plusMinutes(15))) {
//
//                    trips.add(t);
//                } else if (t.getType().equals("Ordinary")) {
//                    List<OrdinaryTrip> stops = ordinaryTripRepository.findAllByTripId(t.getId());
//                    List<String> tripStops = new ArrayList<>();
//
//                    for (OrdinaryTrip o : stops) {
//                        tripStops.add(o.getStopname());
//                    }
//
//                    int startIndex = tripStops.indexOf(start);
//                    int destinationIndex = tripStops.indexOf(destination);
//
//                    if (startIndex != -1 && destinationIndex != -1 &&
//                            startIndex < destinationIndex &&
//                            t.getTime().toLocalTime().isAfter(LocalTime.now().plusMinutes(15))) {
//
//                        trips.add(t);
//                    }
//                }
//            }
//        }
//
//        System.out.println("Returning trips: " + trips);
//        return trips;
//    }
//

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
            String email = JwtAuthenticationFilter.CURRENT_USER;
            User currentUser = userDao.findByEmail(email);
            return bookingService.bookSeat(ticketBooking,trip,currentUser);
        }
        return null;
    }

    public List<Booking> getTickets(long l, String status) {
        return bookingRepository.findAllByPassenger_User_Id(l,status);
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

        int bookedSeatsCount = (int) bookingRepository.countBookedSeatsByTripId(id);
        if ("Luxury".equals(trip.getType())) {
            System.out.println("Booked Seats Count for Luxury Trip: " + bookedSeatsCount);
            // You can add additional handling for Luxury trips here if needed
        } else {
            System.out.println("Booked Seats Count for Ordinary Trip: " + bookedSeatsCount);
        }

       List<OrdinaryTrip> stop = ordinaryTripRepository.findAllByTripId(trip.getId());
        int rem=0;
        for (OrdinaryTrip o:stop) {
            JourneyDetails j= bookingService.getdetails(trip,o.getStopname(),rem);
            rem= j.getRemaining();
            j.setBookedSeatsCount(bookedSeatsCount); // Set booked seats count in JourneyDetails
            journeyDetailsList.add(j);
        }
        return journeyDetailsList;
    }

//    public List<JourneyDetails> getdetails(long id) {
//
//        Trip trip = tripRepository.findById(id).orElse(null);
//        if (trip == null) {
//            return null;
//        }
//
//        List<JourneyDetails> journeyDetailsList = new ArrayList<>();
//
//        // Handle OrdinaryTrip
//        List<OrdinaryTrip> ordinaryStops = ordinaryTripRepository.findAllByTripId(trip.getId());
//        int rem = 0;
//        for (OrdinaryTrip o : ordinaryStops) {
//            JourneyDetails j = bookingService.getdetails(trip, o.getStopname(), rem);
//            rem = j.getRemaining();
//            journeyDetailsList.add(j);
//        }
//
//        // Handle LuxuryTrip
//
//
//
//        if (pickupPoints != null && !pickupPoints.isEmpty()) {
//            for (String pickup : pickupPoints) {
//                JourneyDetails j = bookingService.getdetails(trip, pickup, rem);
//                rem = j.getRemaining();
//                journeyDetailsList.add(j);
//            }
//        }
//
//        if (dropDownPoints != null && !dropDownPoints.isEmpty()) {
//            for (String dropDown : dropDownPoints) {
//                JourneyDetails j = bookingService.getdetails(trip, dropDown, rem);
//                rem = j.getRemaining();
//                journeyDetailsList.add(j);
//            }
//        }
//
//
//        return journeyDetailsList;
//    }

//    public Trip startTrip(long id) {
//        // Fetch the trip by ID, handle the case where the trip is not found
//        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
//
//        // Check the current status and update accordingly
//        if (trip.getStatus() == null) {
//
//            trip.setStatus("RUNNING");
//        } else if (trip.getStatus().equals("RUNNING")) {
//            trip.setStatus("COMPLETED");
//            busService.turnOffBus(trip.getBus().getId(),BusStatus.AVAILABLE);
//            driverService.UnblockDriver(trip.getDriver().getId());
//        }
//
//        // Save the updated trip back to the repository
//        return tripRepository.save(trip);
//    }


    public Trip startTrip(long id, Double latitude, Double longitude) {
        // Fetch the trip by ID, handle the case where the trip is not found
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        // Check the current status and update accordingly
        if (trip.getStatus() == null || trip.getStatus().equals("PENDING")) {
            // Start the trip
            trip.setStatus("RUNNING");

            // Update driver's location and status
            Driver driver = trip.getDriver();
            if (driver != null) {
                driver.setStatus(DriverStatus.BUSY);
                driver.setLatitude(latitude);
                driver.setLongitude(longitude);
                driverService.updateDriver(driver); // Save the updated driver details
            }

            // Update the bus status if necessary
            busService.turnOffBus(trip.getBus().getId(), BusStatus.ON_TRIP);

        } else if (trip.getStatus().equals("RUNNING")) {
            // Complete the trip
            trip.setStatus("COMPLETED");

            // Make the bus available again
            busService.turnOffBus(trip.getBus().getId(), BusStatus.AVAILABLE);

            // Unblock the driver and update location after the trip is completed
            Driver driver = trip.getDriver();
            if (driver != null) {
                driver.setStatus(DriverStatus.ACTIVE);
                driver.setLatitude(latitude);
                driver.setLongitude(longitude);
                driverService.UnblockDriver(driver.getId());
            }
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
       // Date today = Date.valueOf(LocalDate.now());
        List<Trip> allTrips = tripRepository.findTripsFromToday(LocalDate.now());

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

    public List<OrdinaryTrip> findStops(long id) {
        return ordinaryTripRepository.findAllByTripId(id);
    }

    public Trip changeDriver(long id, long driverid) throws Exception {
        Trip t= tripRepository.findById(id).orElse(null);
        if(t!=null){
            t.setDriver(driverService.getVerifiedDriverById(driverid));
            return tripRepository.save(t);
        }
        throw new Exception("Trip not found");
    }


    public List<ToDayBookingDTO> toDayBookingHistry() {
        String email = JwtAuthenticationFilter.CURRENT_USER;

        // Find the vendor by the current user email
        Vendor vendor = vendorDao.findByEmail(email);
        if (vendor == null) {
            throw new RuntimeException("Vendor not found for the email: " + email);
        }

        // Get today's date and the date three days from today
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(2);

        // Fetch trips for the next three days associated with the current vendor
        List<Trip> upcomingTrips = tripRepository.findByVendorIdAndDateBetween(vendor.getId(), today, threeDaysLater);

        List<ToDayBookingDTO> response = new ArrayList<>();
        for (Trip trip : upcomingTrips) {
            String organizationName = vendor.getOrganization_name() != null ? vendor.getOrganization_name() : "N/A";

            ToDayBookingDTO bookingDTO = new ToDayBookingDTO(
                    trip.getBus().getBus_number(),
                    trip.getDriver().getDriver_name(),
                    trip.getTime(),
                    trip.getRoute().getOrigin(),
                    trip.getRoute().getDestination(),
                    organizationName
            );
            response.add(bookingDTO);
        }
        return response;
    }




//    public ResponseEntity<Trip> getTripByPerticularVender(Long id) {
//
//        return tripRepository.TripByPerticularVender(id);
//    }
}

