package com.JustAlo.Service;

import com.JustAlo.Entity.Admin;
import com.JustAlo.Repo.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    public List<Admin> getAllVendor() {
        return adminRepository.findAll();
    }
}
