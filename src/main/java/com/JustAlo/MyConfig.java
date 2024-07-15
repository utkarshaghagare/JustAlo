package com.JustAlo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
class MyConfig {
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails userDetails = User.builder().
//                username("DURGESH")
//                .password(passwordEncoder().encode("DURGESH")).roles("ADMIN").
//                build();
//        return new InMemoryUserDetailsManager(userDetails);
//    }

//
//    public JwtResponse createJwtToken(JwtRequest jwtRequest) throws Exception {
//        String username = jwtRequest.getUsername();
//        String password = jwtRequest.getPassword();
//        authenticate(username, password);
//
//        UserDetails userDetails = loadUserByUsername(username);
//        String newGeneratedToken = jwtUtil.generateToken(userDetails);
//
//        // Determine if the authenticated entity is a user or a vendor
//        User user = userDao.findByUsername(username).orElse(null);
//        if (user != null) {
//            return new JwtResponse(username, newGeneratedToken);
//        } else {
//            Vendor vendor = vendorDao.findByUsername(username).orElse(null);
//            if (vendor != null) {
//                return new JwtResponse(username, newGeneratedToken);
//            } else {
//                throw new Exception("User or Vendor not found with username: " + username);
//            }
//        }
//    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}
