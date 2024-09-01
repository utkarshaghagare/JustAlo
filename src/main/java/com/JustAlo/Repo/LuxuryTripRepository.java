package com.JustAlo.Repo;

import com.JustAlo.Entity.LuxuryTrip;
import com.JustAlo.Entity.OrdinaryTrip;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Model.LuxuryTripModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LuxuryTripRepository extends JpaRepository<LuxuryTrip, Long> {
}