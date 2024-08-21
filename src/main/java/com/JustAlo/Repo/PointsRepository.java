package com.JustAlo.Repo;

import com.JustAlo.Entity.Points;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointsRepository extends JpaRepository<Points, Long> {
}