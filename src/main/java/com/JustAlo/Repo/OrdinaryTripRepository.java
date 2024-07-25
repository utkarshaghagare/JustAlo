package com.JustAlo.Repo;

import com.JustAlo.Entity.OrdinaryTrip;
import com.JustAlo.Entity.Trip;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdinaryTripRepository extends JpaRepository<OrdinaryTrip, Long> {

    List<OrdinaryTrip> findAllByTripId(Trip trip);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrdinaryTrip o WHERE o.trip = :trip")
    void deleteAllByTrip(Trip trip);
}