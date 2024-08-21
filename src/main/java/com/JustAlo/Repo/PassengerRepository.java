package com.JustAlo.Repo;

import com.JustAlo.Entity.Passenger;
import com.JustAlo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Query("SELECT b FROM Passenger b WHERE b.user = :byEmail")
    List<Passenger> findAllByUser(@Param("byEmail") User byEmail);
}