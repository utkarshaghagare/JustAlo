package com.JustAlo.Repo;

import com.JustAlo.Entity.Bus;
import com.JustAlo.Entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByVerifiedTrue();

    @Query(value = "SELECT * FROM bus WHERE vendor_id = :id", nativeQuery = true)
    List<Bus> findByVendorId(@Param("id") Long id);
}
