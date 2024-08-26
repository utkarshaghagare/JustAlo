package com.JustAlo.Controller;


import com.JustAlo.Entity.Vendor;
import com.JustAlo.Model.TicketBooking;
import com.JustAlo.Model.VendorModel;
import com.JustAlo.Service.UserService;
import com.JustAlo.Service.VendorService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3000")

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
    public ResponseEntity<Vendor> registerVendor(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("phone_number") String phone_number,
            @RequestParam("organization_name") String organization_name,
            @RequestParam("address") String address,
            @RequestParam("password") String password,
            @RequestParam("confirm_password") String confirm_password,
            @RequestParam("doc1") MultipartFile doc1,
            @RequestParam("doc2") MultipartFile doc2
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

        Vendor vendor = vendorService.registerVendor(vendorModel);
        return new ResponseEntity<>(vendor, HttpStatus.CREATED);
    }
//    public Vendor registerNewUser(@RequestBody VendorModel vendormodel) {
//        return vendorService.registerVendor(vendormodel);
//    }

    @GetMapping({"/tobeVerified"})
    public List<Vendor> tobeVerified(){
        return vendorService.tobeVerified();
    }

    //@PreAuthorize("hasRole('Vendor')")
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
