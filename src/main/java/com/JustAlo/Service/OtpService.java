package com.JustAlo.Service;

import com.JustAlo.Entity.JwtResponse;
import com.JustAlo.Entity.Role;
import com.JustAlo.Entity.User;
import com.JustAlo.Exception.InvalidOtpException;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.UserDao;
import com.JustAlo.Security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
public class OtpService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;


    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @Value("${spring.mail.host}")
    private String emailHost;

    @Value("${spring.mail.port}")
    private int emailPort;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 30;

    public String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public void saveOtp(String email, String otp) {
        User otpEntity = userDao.findByEmail(email);
        if (otpEntity == null) {
            otpEntity = new User();
            otpEntity.setEmail(email);
        }

        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        User user=new User();
        Role role = roleDao.findById("User")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        user.setRole(userRoles);
        userDao.save(otpEntity);

    }

    public boolean isOtpExpired(String email) {

        User otpEntity = userDao.findByEmail(email);
        if (otpEntity == null) {
            return true;
        }
        return otpEntity.getExpirationTime().isBefore(LocalDateTime.now());
    }
    public void sendOtp(String otp, String destination) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destination);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        javaMailSender.send(message);
    }
    public boolean validateOtp(String email, String otp) {
        Optional<User> otpEntity = userDao.findByEmailAndOtp(email, otp);
        if (otpEntity.isPresent()) {
            if (otpEntity.get().getExpirationTime().isAfter(LocalDateTime.now())) {
                return true;
            } else {
                userDao.delete(otpEntity.get());
            }
        }
        return false;
    }

//    public void updateOtp(String email, String newOtp) {
//        User otpEntity = userDao.findByEmail(email);
//        if (otpEntity == null) {
//            otpEntity = new User();
//            otpEntity.setEmail(email);
//        }
//        otpEntity.setOtp(newOtp);
//        otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(5)); // Set OTP expiration time to 5 minutes from now
//        userDao.save(otpEntity);
//    }
//    public boolean isOtpExpired(String email) {
//        User otpEntity = userDao.findByEmail(email);
//        if (otpEntity == null) {
//            return true;
//        }
//        return otpEntity.getExpirationTime().isBefore(LocalDateTime.now());
//    }
//    public String generateOtp(String email) {
//        // Logic to generate a new OTP
//        return String.valueOf((int) ((Math.random() * (999999 - 100000)) + 100000)); // Example: generate a 6-digit OTP
//    }
}