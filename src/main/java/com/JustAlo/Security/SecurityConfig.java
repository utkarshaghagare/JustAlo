package com.JustAlo.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
            "/registerAdmin",
            "/buses/upload",
            "/registerNewVendor",
            "/registerAdmin",
            "/auth/login" ,
            "/registerNewUser",
            "/findTrip",
            "/authenticate",
            "/send-otp",
            "/validate-otp",
            "/getfindTrip",
            "/buses/**",
            "/addDriver",
            "/trips/**",
            "/available_seats/{id}",
            "/BookSeat",
            "/Tickets",
            "/getAllDriver",
            "/trips/getAllRoute",
            "/getdetails/{id}",
            "/BookSeat"

    };


    @Autowired
    private JwtAuthenticationEntryPoint point;
    @Autowired
    private JwtAuthenticationFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeRequests().
                requestMatchers("/test").authenticated().requestMatchers(PUBLIC_URLS).permitAll()
                .anyRequest()
                .authenticated()
                .and().exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
