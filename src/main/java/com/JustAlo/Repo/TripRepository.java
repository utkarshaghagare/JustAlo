package com.JustAlo.Repo;

import com.JustAlo.Entity.LuxuryTrip;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {



    List<Trip> findAllByVendor(Vendor id);

    List<Trip> findAllByDate(LocalDate date);
    Optional<Trip> findByDateAndTime(Date date, Time time);

    List<Trip> findByDriverId(Long id);

    @Query(value = "SELECT * FROM trip WHERE date >= :today", nativeQuery = true)
    List<Trip> findTripsFromToday(@Param("today") LocalDate today);
    @Query(value = "SELECT * FROM trip WHERE vendor_id = :id", nativeQuery = true)
    List<Trip> findTripsByVendorId(@Param("id") Long id);

    List<Trip> findByDriverIdAndDate(Long driverId, Date date);


    List<Trip> findByVendorIdAndDate(Long id, Date today);



    @Query("SELECT t FROM Trip t WHERE t.vendor.id = :vendorId AND t.date BETWEEN :startDate AND :endDate")
    List<Trip> findByVendorIdAndDateBetween(@Param("vendorId") Long vendorId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}