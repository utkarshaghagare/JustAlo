package com.JustAlo.Repo;

import com.JustAlo.Entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByVerifiedTrue();

}
