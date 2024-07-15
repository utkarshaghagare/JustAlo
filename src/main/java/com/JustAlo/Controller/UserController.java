package com.JustAlo.Controller;


import com.JustAlo.Service.JwtService;
import com.JustAlo.Service.UserService;
import com.JustAlo.Entity.JwtRequest;
import com.JustAlo.Entity.JwtResponse;
import com.JustAlo.Entity.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

@Autowired
private JwtService jwtService;

    @PostConstruct
    public void initRoleAndUser() {
        userService.initRoleAndUser();
    }

    @PostMapping({"/registerNewUser"})
    public User registerNewUser(@RequestBody User user) {
        return userService.registerNewUser(user);
    }

    @PostMapping("/auth/login")
    public JwtResponse createJwtToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        return jwtService.createJwtToken(jwtRequest);
    }

    @PostMapping({"/registerAdmin"})
    public User registerNewAdmin(@RequestBody User user) {
        return userService.registerAdmin(user);
    }

//    @PostMapping({"/updateUser/{id}"})
//    public User updateUser(@PathVariable("id") Long id,@RequestBody User user) {
//        return userService.updateUser(id,user);
//    }

    @GetMapping({"/forAdmin"})
    @PreAuthorize("hasRole('Admin')")
    public String forAdmin(){
        return "This URL is only accessible to the admin";
    }

    @GetMapping({"/forUser"})
    @PreAuthorize("hasRole('User')")
    public String forUser(){
        return "This URL is only accessible to the user";
    }


//    @GetMapping("/getAllUser")
//    @PreAuthorize("hasRole('Vendor')")
//
//    public List<User> getAllUser(){
//
//        return userService.getAllUser();
//
//
//    }
}
