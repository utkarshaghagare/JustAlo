package com.JustAlo.Repo;

import com.JustAlo.Entity.Trip;
import com.JustAlo.Entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findAllByVendor(Vendor id);

    List<Trip> findAllByDate(Date date);
}