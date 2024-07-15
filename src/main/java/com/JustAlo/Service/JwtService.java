package com.JustAlo.Service;

import com.JustAlo.Entity.*;
import com.JustAlo.Repo.DriverDao;
import com.JustAlo.Repo.VendorDao;
import com.JustAlo.Security.JwtHelper;
import com.JustAlo.Repo.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;


@Service
public class JwtService implements UserDetailsService {

    @Autowired
    private JwtHelper jwtUtil;

    @Autowired
    private UserDao userDao;

    @Autowired
    private VendorDao vendorDao;

    @Autowired
    private DriverDao driverDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtResponse createJwtToken(JwtRequest jwtRequest) throws Exception {
        String email = jwtRequest.getEmail();
        String password = jwtRequest.getPassword();
        String otp = jwtRequest.getOtp();

        authenticate(email, password,otp);

        UserDetails userDetails = loadUserByUsername(email);
        String newGeneratedToken = jwtUtil.generateToken(userDetails);

        // Determine if the authenticated entity is a user, vendor, or driver
        User user = userDao.findByEmail(email);
        if (user != null) {
            return new JwtResponse(email, newGeneratedToken);
        } else {
            Vendor vendor = vendorDao.findByEmail(email);
            if (vendor != null) {
                return new JwtResponse(email, newGeneratedToken);
            } else {
                Driver driver= driverDao.findByEmail(email).orElse(null);
                if (driver !=null) {
                    return new JwtResponse(email, newGeneratedToken);
                } else {
                    throw new Exception("User, Vendor, or Driver not found with email: " + email);
                }
            }
        }
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        User user = userDao.findByEmail(email);
        Vendor vendor = vendorDao.findByEmail(email);
        Optional<Driver> driverOptional = driverDao.findByEmail(email);
        if (user == null && vendor == null && driverOptional == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Collection<GrantedAuthority> authorities = new HashSet<>();
        String password = null;
        if (user != null) {
            user.getRole().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
            });
            password = user.getOtp();
        } else if (vendor != null) {
            vendor.getRole().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
            });
            password = vendor.getPassword();

        } else if (driverOptional.isPresent()) {
            Driver driver = driverOptional.get();
            driver.getRole().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
            });
            password = driver.getPassword();

        }

        return new org.springframework.security.core.userdetails.User(email, password, authorities);

    }

    private void authenticate(String email, String password, String otp) throws Exception {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            } catch (DisabledException e) {
                throw new Exception("USER_DISABLED", e);
            } catch (BadCredentialsException e) {
                throw new Exception("INVALID_CREDENTIALS", e);
            }
        }}

