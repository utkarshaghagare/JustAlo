package com.JustAlo.Controller;


import com.JustAlo.Entity.Admin;
import com.JustAlo.Entity.User;
import com.JustAlo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @Autowired
   private UserService userService;

    @PostMapping({"/registerAdmin"})
    public Admin registerNewAdmin(@RequestBody Admin admin) {
        return userService.registerAdmin(admin);
    }
}
