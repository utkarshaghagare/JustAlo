package com.JustAlo.Repo;

import com.JustAlo.Entity.Driver;
import com.JustAlo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverDao extends JpaRepository<Driver, Long> {

    public static String findById(String currentUser) {
        return currentUser;
    }

    Optional<Driver> findByEmail(String email);

    @Query("SELECT d.verification_status FROM Driver d WHERE d.id = :id")
    Boolean isDriverVerified(@Param("id") Long id);

    @Query("SELECT d FROM Driver d WHERE d.verification_status = :verificationStatus")
    List<Driver> findByVerificationStatus(@Param("verificationStatus") Boolean verificationStatus);

    @Query(value = "SELECT * FROM driver WHERE vendor_id = :id", nativeQuery = true)
    List<Driver> findByVendorId(@Param("id") Long id);

    @Query(value = "SELECT * FROM driver WHERE verification_status = false", nativeQuery = true)
    List<Driver> findUnverifiedDrivers();

//    @Query(value = "SELECT * FROM driver WHERE verification_status = true", nativeQuery = true)
//    List<Driver> findverifiedDrivers();
}