package com.JustAlo.Repo;

import com.JustAlo.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,Long> {

    Admin findByEmail(String email);

}
