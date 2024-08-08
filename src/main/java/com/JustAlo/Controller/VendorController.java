package com.JustAlo.Controller;


import com.JustAlo.Entity.Vendor;
import com.JustAlo.Model.TicketBooking;
import com.JustAlo.Model.VendorModel;
import com.JustAlo.Service.UserService;
import com.JustAlo.Service.VendorService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class VendorController {
    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @PostConstruct
    public void initRoleAndVendor() {
    	userService.initRoleAndUser();
    }

    @PostMapping({"/registerNewVendor"})
    public Vendor registerNewUser(@RequestBody VendorModel vendormodel) {
        return vendorService.registerVendor(vendormodel);
    }

    @GetMapping({"/tobeVerified"})
    public List<Vendor> tobeVerified(){
        return vendorService.tobeVerified();
    }

    @PreAuthorize("hasRole('Vendor')")
    @PostMapping("/markVerified/{id}")
    public Vendor markVerified(@PathVariable("id") Long id) throws Exception {
        return vendorService.markVerified(id);
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
    
}
