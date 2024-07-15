package com.JustAlo.Repo;

import com.JustAlo.Entity.Driver;
import com.JustAlo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverDao extends JpaRepository<Driver, Long> {

    Optional<Driver> findByEmail(String email);


}
