package com.JustAlo.Controller;


import com.JustAlo.Entity.*;
import com.JustAlo.Model.*;
import com.JustAlo.Model.enums.DriverStatus;
import com.JustAlo.Repo.BookingRepository;
import com.JustAlo.Repo.TripRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import com.JustAlo.Service.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping
public class VendorController {
    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private BusService busService;

    @Autowired
    private DriverService driverService;
    @Autowired
    private TripService tripService;

    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private BookingRepository bookingRepository;


    @Autowired
    private PaymentService paymentService;
    @PostConstruct
    public void initRoleAndVendor() {
    	userService.initRoleAndUser();
    }

    @PostMapping({"/registerNewVendor"})
    public ResponseEntity<Vendor> registerVendor(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("phone_number") String phone_number,
            @RequestParam("organization_name") String organization_name,
            @RequestParam("address") String address,
            @RequestParam("password") String password,
            @RequestParam("confirm_password") String confirm_password,
            @RequestParam("doc1") MultipartFile doc1,
            @RequestParam("doc2") MultipartFile doc2,
            @RequestParam("profile_img")MultipartFile profile_img
    ) {
        VendorModel vendorModel = new VendorModel();
        vendorModel.setUsername(username);
        vendorModel.setEmail(email);
        vendorModel.setPhone_number(phone_number);
        vendorModel.setOrganization_name(organization_name);
        vendorModel.setAddress(address);
        vendorModel.setPassword(password);
        vendorModel.setConfirm_password(confirm_password);
        vendorModel.setDoc1(doc1);
        vendorModel.setDoc2(doc2);
        vendorModel.setProfile_img(profile_img);

        Vendor vendor = vendorService.registerVendor(vendorModel);
        return new ResponseEntity<>(vendor, HttpStatus.CREATED);
    }
//    public Vendor registerNewUser(@RequestBody VendorModel vendormodel) {
//        return vendorService.registerVendor(vendormodel);
//    }

    @GetMapping({"/tobeVerified"})
    @PreAuthorize("hasRole('Admin')")
    public List<Vendor> tobeVerified(){
        return vendorService.tobeVerified();
    }


    @PostMapping("/markVerified/{id}")
    @PreAuthorize("hasRole('Admin')")
    public Vendor markVerified(@PathVariable("id") Long id) throws Exception {
        return vendorService.markVerified(id);
    }

    @PostMapping("/markUnverified/{id}")
    @PreAuthorize("hasRole('Admin')")
    public Vendor markUnverified(@PathVariable("id") Long id) throws Exception {
        return vendorService.markUnverified(id);
    }

//    @PostMapping("/BookSeat")
//    @PreAuthorize("hasRole('Vendor')")
//    public String bookSeat(@RequestBody TicketBooking ticketBooking) throws Exception {
//        return vendorService.bookSeat(ticketBooking);
//    }
//    @PreAuthorize("hasRole('Vendor')")
//    @PostMapping("/reserveSeat/{tripId}")
//    public String reserveSeat(@PathVariable("tripId") Long id, @RequestBody List<Integer> seats) throws Exception {
//        return vendorService.reserveSeat(id,seats);
//    }
//get trips
    //get trips from date to date


    //Bus

    @GetMapping({"/turnOffBus/{id}"})
    @PreAuthorize("hasRole('Vendor')")
    public String turnOffBus(@PathVariable("id") Long id){
        return busService.turnOffBus(id, BusStatus.OUT_OF_SERVICE);
    }
    @GetMapping({"/turnONBus/{id}"})
    @PreAuthorize("hasRole('Vendor')")
    public String turnONBus(@PathVariable("id") Long id){
        return busService.turnOffBus(id, BusStatus.AVAILABLE);
    }

    @GetMapping({"/turnOffDriver/{id}"})
    @PreAuthorize("hasRole('Vendor')")
    public String turnOffDriver(@PathVariable("id") Long id){
        return  driverService.turnOffDriver(id, DriverStatus.UNAVAILABLE);
    }

    @GetMapping({"/turnONDriver/{id}"})
    @PreAuthorize("hasRole('Vendor')")
    public String turnONDriver(@PathVariable("id") Long id){
        return  driverService.turnONDriver(id, DriverStatus.ACTIVE);
    }

    @GetMapping("/verifiedAndAvailableDriver")
    @PreAuthorize("hasRole('Vendor')")
    public List<Driver> getAllVerifiedDriver() {
        List<Driver> drivers = driverService.getAllVerifiedAvailableDrivers();
        return drivers;
    }
    @GetMapping("/verifiedAndAvailableBus")
    @PreAuthorize("hasRole('Vendor')")
    public List<Bus> getAllVerifiedBuses(){
        List<Bus> bus =busService.getAllVerifiedBuses();
        return bus;


    }
    @GetMapping("/tripsByVendor/{id}")
    @PreAuthorize("hasAnyRole('Admin')")
    public ResponseEntity<List<Trip>> getTripsByVendor(@PathVariable("id") Long id) {
        List<Trip> trips = tripRepository.findTripsByVendorId(id);
        if (trips.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(trips);
        }
    }


    @GetMapping("/TodayTripsByVendor/{id}")
    @PreAuthorize("hasAnyRole('Vendor', 'Admin')")
    public ResponseEntity<List<Trip>> getTodayTripsByVendor(@PathVariable("id") Long id) {
        List<Trip> trips = tripRepository.findTripsByVendorId(id);
        if (trips.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            List<Trip> response= new ArrayList<>();
            for(Trip t:trips){
                if(t.getDate().equals(Date.valueOf(LocalDate.now()))){
                    response.add(t);
                }
            }
            return ResponseEntity.ok(response);
        }
    }

    //Not Tested
    @GetMapping("/BookingByTrip/{id}")
    @PreAuthorize("hasAnyRole('Vendor', 'Admin')")
    public ResponseEntity<List<Booking>> getBookingByTrip(@PathVariable("id") Long id) {
        List<Booking> bookings = bookingRepository.findAllByTrip(tripRepository.findById(id).orElse(null));
        if (bookings.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            bookings.removeIf(b -> b.getPassenger() == null);
            return ResponseEntity.ok(bookings);
        }
    }
    @GetMapping("/BookingChartByTrip/{id}")
    @PreAuthorize("hasAnyRole('Vendor', 'Admin')")
    public ResponseEntity<List<Booking>> getBookingChartByTrip(@PathVariable("id") Long id) {
        List<Booking> bookings = bookingRepository.findAllByTrip(tripRepository.findById(id).orElse(null));
        if (bookings.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            bookings = bookings.stream()
                    .filter(b -> "BOOKED".equals(b.getStatus()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bookings);
        }
    }

    //Ticket
    @GetMapping("Vendor/Tickets/BookedByVendor")
    @PreAuthorize("hasRole('Vendor')")
    public List<Booking> getTickets() throws Exception {
        return tripService.getTickets(0L,"BOOKED");
    }
    @GetMapping("Vendor/CancelTicket/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public void cancelTicket(@PathVariable long id) throws Exception {
        tripService.cancelTicket(id);
    }
    //Yet to be tested
    @GetMapping("/Vendor/Tickets/cancelled")
    @PreAuthorize("hasRole('Vendor')")
    public List<Booking> getCancelledTickets() throws Exception {
        return tripService.getTickets(0L, "CANCELLED");
    }


    @GetMapping("/CancelTicketCountByTrip/{tripid}")
    @PreAuthorize("hasAnyRole('Vendor', 'Admin')")
    public ResponseEntity<Long> CancelTicketCountByTrip(@PathVariable("tripid") Long tripid) {
        // Fetch the trip by ID and handle the case where it might not be found
        Trip trip = tripRepository.findById(tripid).orElse(null);
        if (trip == null) {
            // Return 404 NOT FOUND if the trip is not found
            return ResponseEntity.notFound().build();
        }

        // Fetch all bookings for the found trip
        List<Booking> bookings = bookingRepository.findAllByTrip(trip);

        // Count bookings with status "CANCELLED"
        long cancelledCount = bookings.stream()
                .filter(b -> "CANCELLED".equals(b.getStatus()))
                .count();

        // Return the count wrapped in a ResponseEntity
        return ResponseEntity.ok(cancelledCount);
    }

    @GetMapping("/BookedTicketCountByTrip/{tripid}")
    @PreAuthorize("hasAnyRole('Vendor', 'Admin')")
    public ResponseEntity<Long> BookedTicketCountByTrip(@PathVariable("tripid") Long tripid) {
        // Fetch the trip by ID and handle the case where it might not be found
        Trip trip = tripRepository.findById(tripid).orElse(null);
        if (trip == null) {
            // Return 404 NOT FOUND if the trip is not found
            return ResponseEntity.notFound().build();
        }

        // Fetch all bookings for the found trip
        List<Booking> bookings = bookingRepository.findAllByTrip(trip);

        // Count bookings with status "CANCELLED"
        long cancelledCount = bookings.stream()
                .filter(b -> "BOOKED".equals(b.getStatus()))
                .count();

        // Return the count wrapped in a ResponseEntity
        return ResponseEntity.ok(cancelledCount);
    }
    @GetMapping("Vendor/ChangeDriver/{tripid}/{driverid}")
    @PreAuthorize("hasRole('Vendor')")
    public Trip changeDriver(@PathVariable("tripid") Long tripid, @PathVariable("driverid") Long driverid) throws Exception {
        return tripService.changeDriver(tripid,driverid);
    }

    @GetMapping("/toDayBookingHistry")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<List<ToDayBookingDTO>> toDayBookingHistry() {
        List<ToDayBookingDTO> bookings = tripService.toDayBookingHistry();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/getAllBusByPerticularVendor")
    @PreAuthorize("hasRole('Vendor')")
    public List<Bus> getAllBusByPerticularVendor() {
        return busService.getAllBusByPerticularVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER).getId());
    }
    @GetMapping("/AllDriverByPerticularVendor")
    @PreAuthorize("hasRole('Vendor')")
    public  List<Driver> getAllDriverByPerticularVendor(){
        return driverService.getAllDriverByPerticularVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER).getId());

    }
    @GetMapping("/tripsByVendor")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<List<Trip>> getTripsByVendor() {
        List<Trip> trips = tripRepository.findTripsByVendorId(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER).getId());
        if (trips.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(trips);
        }
    }
    @GetMapping("/getpaymentinformation")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<TransactionDTO>> getAllDetails() {
        return paymentService.getAllDetails();
    }

//Offer Section
@PostMapping("/makeOfferRequest")
@PreAuthorize("hasRole('Vendor')")
public RequestOffers makeOfferRequest(@RequestBody offerInput input) throws Exception {
    return vendorService.makeOfferRequest(input.id,input.percent);
}

    @GetMapping("/makeOfferRequest")
    @PreAuthorize("hasRole('Admin')")
    public List<RequestOffers> getOfferRequest() throws Exception {
        return vendorService.getOfferRequest();
    }

    @PostMapping("/makeOffer")
    @PreAuthorize("hasRole('Admin')")
    public Offers makeOffer(
                                   @RequestParam("trips") List<RequestOffers> requestOffersList,
                                   @RequestParam("banner") MultipartFile image,
                                   @RequestParam("percent") double percent) throws Exception {
        return vendorService.makeOffer(requestOffersList,image,percent);
    }

    @Autowired
    BookingService bookingService;
    //Api for vendor to book seat of trip which is scheduled by only them
    @PostMapping("/vendor/bookSeat")
    @PreAuthorize("hasRole('Vendor')")
    public String bookSeat( @RequestBody TicketBooking ticketBooking) throws Exception {
        Optional<Trip> trip= tripRepository.findById(ticketBooking.getTrip_id());
        Vendor vendor=vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER);
        if(trip== null ){
            throw new Exception("Trip not found");
        }
        else if (trip.get().getVendor()!= vendor ){
            throw new Exception("Trip is not scheduled by you");
        }
        else{
            return bookingService.bookSeat(ticketBooking,trip.get(),null);
        }

    }
}
