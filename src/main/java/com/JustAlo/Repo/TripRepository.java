package com.JustAlo.Repo;

import com.JustAlo.Entity.Trip;
import com.JustAlo.Entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findAllByVendor(Vendor id);

    List<Trip> findAllByDate(Date date);
    Optional<Trip> findByDateAndTime(Date date, Time time);

    List<Trip> findByDriverId(Long id);

    @Query("SELECT t FROM Trip t WHERE t.date >= :today")
    List<Trip> findTripsFromToday(@Param("today") Date today);
    @Query(value = "SELECT * FROM Trip WHERE vendor_id = :id", nativeQuery = true)
    List<Trip> findTripsByVendorId(@Param("id") Long id);

}