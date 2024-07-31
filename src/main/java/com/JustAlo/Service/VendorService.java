package com.JustAlo.Service;


import com.JustAlo.Entity.Role;
import com.JustAlo.Entity.Vendor;
import com.JustAlo.Model.VendorModel;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.VendorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VendorService {
    @Autowired
    private VendorDao vendorDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;


    //exception
    public Vendor registerVendor(VendorModel vendormodel) {


        if (vendormodel.password.equals(vendormodel.confirm_password)) {

            if(vendorDao==null){
                return saveVendor(vendormodel);
            }
            else{
                Optional<Vendor> vendor1 = vendorDao.findByUsername(vendormodel.getUsername());
                if (vendor1.isPresent()) {
                    //throw new RuntimeException();
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor Already Present");

                }
                else{
                    return saveVendor(vendormodel);
                }
            }
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password not matching");
    }

    public Vendor saveVendor(VendorModel vendormodel){

        Vendor vendor = new Vendor();
        Role role = roleDao.findById("Vendor").get();
        Set<Role> venderRoles = new HashSet<>();
        venderRoles.add(role);
        vendor.setRole(venderRoles);
        vendor.setPassword(getEncodedPassword(vendormodel.getPassword()));
        vendor.setAddress(vendormodel.getAddress());
        vendor.setEmail(vendormodel.getEmail());
        vendor.setUsername(vendormodel.getUsername());
        vendor.setOrganization_name(vendormodel.getOrganization_name());
        vendor.setPhone_number(vendormodel.getPhone_number());
        vendor.setVerification_status(false);
        vendor.setDoc1(vendormodel.getDoc1());
        vendor.setDoc2(vendormodel.getDoc2());

        return vendorDao.save(vendor);
    }  public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public List<Vendor> tobeVerified() {
        return vendorDao.findUnverifiedVendors();
    }

    public Vendor markVerified(Long id) throws Exception {
        Optional<Vendor> optionalVendor = vendorDao.findById(id);
        if (!optionalVendor.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor with ID " + id + " not found");
        }
        Vendor vendor = optionalVendor.get();
        vendor.setVerification_status(true);

        vendorDao.save(vendor);
        return vendor;
    }

    public Vendor findByUsername(String currentUser) {
        return vendorDao.findByEmail(currentUser);
    }

    public Vendor findById(Long id) {
        return vendorDao.findById(id).orElse(null);
    }
}



