package com.JustAlo.Controller;


import com.JustAlo.Entity.*;
import com.JustAlo.Model.*;
import com.JustAlo.Model.enums.UserStatus;
import com.JustAlo.Repo.BookingRepository;
import com.JustAlo.Repo.DriverDao;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.UserDao;
import com.JustAlo.Security.JwtAuthenticationFilter;
import com.JustAlo.Security.JwtHelper;
import com.JustAlo.Service.*;

import com.razorpay.Order;
import jakarta.annotation.PostConstruct;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class UserController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private TripService tripService;
    @Autowired
    private JwtHelper jwtUtil;
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DriverDao driverDao;

    @PostConstruct
    public void initRoleAndUser() {
        userService.initRoleAndUser();
    }

//USER CRUD
//registration
//    @PostMapping({"/registerNewUser"})
//    public User registerNewUser(@RequestBody User user) {
//        return userService.registerNewUser(user);
//    }

//Login
    @PostMapping("/auth/login")
    public JwtResponse createJwtToken(@RequestBody JwtRequest jwtRequest) throws Exception {
//        Optional<Driver> driver =driverDao.findByEmail(jwtRequest.getEmail());
//        if( driver.get().getStatus() == DriverStatus.BLOCKED) {
//            throw new BadRequestException("User is blocked");
//        }
        return jwtService.createJwtToken(jwtRequest);
    }
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) throws BadRequestException {
        User user = userDao.findByEmail(email);

        if (user == null) {
            // Create a new user if not found
            user = new User();
            user.setEmail(email);
            // Assign a default role
            Role role = roleDao.findById("User")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(role);
            user.setRole(userRoles);
            userDao.save(user);
        }

        if( user.getStatus() == UserStatus.BLOCKED) {
            throw new BadRequestException("User is blocked");
        }

        // Check if OTP exists and if it is expired
        if (user.getOtp() != null && user.getExpirationTime().isAfter(LocalDateTime.now())) {
            return ResponseEntity.ok("OTP already sent and valid.");
        }

        // Generate and save new OTP
        String otp = otpService.generateOtp();
        otpService.saveOtp(email, otp);
        otpService.sendOtp(otp, email);

        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateOtp(email, otp)) {
            User user = userService.findByEmail(email);
            if (user == null) {
                //   user = new User();
                user.setEmail(email);

                // Assign a default role

                Role role = roleDao.findById("User")
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(role);
                user.setRole(userRoles);
                userDao.save(user);
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(email);

            return ResponseEntity.ok(new JwtResponse(email, token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }
    }
//Profile Section
    @PutMapping({"/updateUser/{id}"})
    public User updateUser(@PathVariable("id") Long id,@RequestBody User user) {
        return userService.updateUser(id,user);
    }

                 //acess- admin , user
    @PutMapping({"/deleteUser/{id}"})
    public void updateUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }


//Add to admin contoller
//    @PostMapping({"/registerAdmin"})
//    public User registerNewAdmin(@RequestBody User user) {
//        return userService.registerAdmin(user);
//    }

    @GetMapping("/getAllUser")
    @PreAuthorize("hasRole('Admin')")
    public List<User> getAllUser(){
        return userService.getAllUser();

    }
//BOOKING Section

    //search trip-> input start end time/NO TIME
    @GetMapping("/findTrip")
   // @PreAuthorize("hasRole('Vendor')")
    public List<Trip> findTrip(@RequestBody TripRequest tripRequest){
        return tripService.findTrip(tripRequest.getStart() ,tripRequest.getDestination(),tripRequest.getDate());
    }

    @PostMapping("/findTrip")
  //  @PreAuthorize("hasRole('Vendor','User')")
    public List<Trip> findTrip1(@RequestBody TripRequest tripRequest){
        return tripService.findTrip(tripRequest.getStart() ,tripRequest.getDestination(),tripRequest.getDate());
    }

    @GetMapping("/findStops/{id}")
    @PreAuthorize("hasAnyRole('Vendor','User')")
    public List<OrdinaryTrip> findStops(@PathVariable long id){
        return tripService.findStops(id);
    }

//    @GetMapping("/findLocationOfPoints/{id}")
//    @PreAuthorize("hasRole('Vendor','User')")
//    public List<OrdinaryTrip> findStops(@PathVariable long id){
//        return tripService.findStops(id);
//    }
    @GetMapping("/findRecentRoutes")
    @PreAuthorize("hasRole('User')")
    public List<RecentBookingsRoute> findRecentTrips(){
        return tripService.findRecentTrips();
    }
    @GetMapping("/findTripsfromOrigin")
    @PreAuthorize("hasAnyRole('Vendor','User')")
    public List<Trip> findTripsFromOrigin(@RequestBody UserLocation u ){
        return tripService.findTripsFromOrigin(u.longitude,u.latitude);
    }


    //select start point end point
    //working
    @PostMapping("/available_seats/{id}")
   // @PreAuthorize("hasRole('User')")
    public Seats findSeat(@RequestBody TripRequest tripRequest, @PathVariable long id){
        return tripService.findSeat(tripRequest.getStart() ,tripRequest.getDestination(),id);
    }


    //select seats
    //provide passanger details
    //save
    //yet to be tested
        @GetMapping("/passengers")
    @PreAuthorize("hasRole('User')")
    public List<Passenger> getPassenger() throws Exception {
        return tripService.getPassengers();
    }
    @PostMapping("/BookSeat")
    @PreAuthorize("hasRole('User')")
    public String bookSeat(@RequestBody TicketBooking ticketBooking) throws Exception {
        return tripService.bookSeat(ticketBooking);
    }




    //payment
//    @PostMapping("/payment")
//    @PreAuthorize("hasRole('User')")
//    public ResponseEntity<String> paymentamonut(@RequestParam double amount){
//        try{
//              TicketBooking ticketBooking= new TicketBooking();
//            double totalAmount =   ticketBooking.getAmount();
//            Order order=paymentService.bookedTicket(totalAmount);
//            User user = userDao.getById(ticketBooking.getUser_id());
//
//            Transaction transaction=paymentService.saveTransaction(order.get("id"),totalAmount,"INR","created",user);
//            return ResponseEntity.ok("Order created successfully with transaction ID: " + transaction.getId());
//
//        }catch (Exception e){
//            return ResponseEntity.status(500).body("Failed to create order: " + e.getMessage());
//
//        }
//    }

    @PostMapping("/payment")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<String> paymentAmount(@RequestParam double amount) {
        try {
            // Assuming the user ID is passed as a parameter
            User user = userDao.findByEmail((JwtAuthenticationFilter.CURRENT_USER));

            // Create a Razorpay order
            Order order = paymentService.bookedTicket(amount);


            // Save the transaction
            Transaction transaction = paymentService.saveTransaction(order.get("id"), amount, "INR", "created", user);
              transaction.getAmount();

            return ResponseEntity.ok("Order created successfully with transaction ID: " + transaction.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create order: " + e.getMessage());
        }
    }


//Tickets Section
    //Yet to be tested
    @GetMapping("/Tickets/booked")
    @PreAuthorize("hasRole('User')")
    public List<Booking> getTickets() throws Exception {
        return tripService.getTickets( userDao.findByEmail(JwtAuthenticationFilter.CURRENT_USER).getId(),"BOOKED");
    }
    @GetMapping("/CancelTicket/{id}")
    @PreAuthorize("hasRole('User')")
    public void cancelTicket(@PathVariable long id) throws Exception {
        tripService.cancelTicket(id);
    }
    //Yet to be tested
    @GetMapping("/Tickets/cancelled")
    @PreAuthorize("hasRole('User')")
    public List<Booking> getCancelledTickets() throws Exception {
        return tripService.getTickets(userDao.findByEmail(JwtAuthenticationFilter.CURRENT_USER).getId(),"CANCELLED");
    }

    //Mark all has roleUser

    @Autowired
    private RentService rentService;

    @PostMapping("/rent/enquiry")
    public Rent addRentWithVehicles(@RequestBody RentRequest rentRequest) {
        return rentService.addRentWithVehicles(rentRequest.rent, rentRequest.vehicles);
    }

}




