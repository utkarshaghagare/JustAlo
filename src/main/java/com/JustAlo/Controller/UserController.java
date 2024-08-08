package com.JustAlo.Controller;


import com.JustAlo.Entity.*;
import com.JustAlo.Model.TicketBooking;
import com.JustAlo.Model.TripRequest;
import com.JustAlo.Repo.BookingRepository;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.UserDao;
import com.JustAlo.Security.JwtHelper;
import com.JustAlo.Service.*;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @PostConstruct
    public void initRoleAndUser() {
        userService.initRoleAndUser();
    }

//USER CRUD
    @PostMapping({"/registerNewUser"})
    public User registerNewUser(@RequestBody User user) {
        return userService.registerNewUser(user);
    }

    @PostMapping("/auth/login")
    public JwtResponse createJwtToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        return jwtService.createJwtToken(jwtRequest);
    }

    @PutMapping({"/updateUser/{id}"})
    public User updateUser(@PathVariable("id") Long id,@RequestBody User user) {
        return userService.updateUser(id,user);
    }

    @PutMapping({"/deleteUser/{id}"})
    public void updateUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }

    @PostMapping({"/registerAdmin"})
    public User registerNewAdmin(@RequestBody User user) {
        return userService.registerAdmin(user);
    }

    @GetMapping({"/forAdmin"})
    @PreAuthorize("hasRole('Admin')")
    public String forAdmin() {
        return "This URL is only accessible to the admin";
    }

    @GetMapping({"/forUser"})
    @PreAuthorize("hasRole('User')")
    public String forUser() {
        return "This URL is only accessible to the user";
    }


    @GetMapping("/getAllUser")
    @PreAuthorize("hasRole('User')")
    public List<User> getAllUser(){
        return userService.getAllUser();

    }

    //OTP LOGIN SYSTEM

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
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


    //search trip-> input start end time/NO TIME
    @GetMapping("/findTrip")
    @PreAuthorize("hasRole('Vendor')")
    public List<Trip> findTrip(@RequestBody TripRequest tripRequest){
        return tripService.findTrip(tripRequest.getStart() ,tripRequest.getDestination(),tripRequest.getDate());
    }

    //select start point end point
    //working
    @GetMapping("/available_seats/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public List<Integer> findSeat(@RequestBody TripRequest tripRequest, @PathVariable long id){
        return tripService.findSeat(tripRequest.getStart() ,tripRequest.getDestination(),id);
    }


//select seats
//provide passanger details
//save
    @PostMapping("/BookSeat")
    @PreAuthorize("hasRole('Vendor')")
    public String bookSeat(@RequestBody TicketBooking ticketBooking) throws Exception {
        return tripService.bookSeat(ticketBooking);
    }


//payment

//Yet to be tested
    @GetMapping("/Tickets")
    @PreAuthorize("hasRole('Vendor')")
    public List<Booking> getTickets() throws Exception {
        return tripService.getTickets();
    }

    //Mark all has roleUser


}




