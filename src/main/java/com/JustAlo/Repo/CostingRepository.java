package com.JustAlo.Repo;

import com.JustAlo.Entity.Costing;
import com.JustAlo.Entity.LuxuryTrip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostingRepository extends JpaRepository<Costing, Long> {
}