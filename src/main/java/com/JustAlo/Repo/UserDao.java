package com.JustAlo.Repo;


import com.JustAlo.Entity.User;
import com.JustAlo.Entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    Optional<User> findById(long id);

//    Optional<User> findByUsername(String username);
   // Optional<User> findByEmail(String email);
   // Optional<User> findByPhone(String phone);
    User findByEmail(String email);


    Optional<User> findByEmailAndOtp(String email, String otp);

}
