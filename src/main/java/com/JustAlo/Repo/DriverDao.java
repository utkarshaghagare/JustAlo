package com.JustAlo.Repo;

import com.JustAlo.Entity.Driver;
import com.JustAlo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverDao extends JpaRepository<Driver, Long> {

    Optional<Driver> findByEmail(String email);


    @Query("SELECT d FROM Driver d WHERE d.verification_status = :verificationStatus")
    List<Driver> findByVerificationStatus(@Param("verificationStatus") Boolean verificationStatus);}