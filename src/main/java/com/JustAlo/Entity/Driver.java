package com.JustAlo.Entity;

import com.JustAlo.Model.BusStatus;
import com.JustAlo.Model.enums.DriverStatus;
import com.JustAlo.Model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    public Vendor vendor;
    private String driver_name;
    private String driver_nickname;
    private String email;
    private String password;
    private String mobile_no;
    private String license_no;
    private String address;
    private String aadhar_no;
    private  String driver_img;
    private String id_proof_img;
    private String license_img;
    private Boolean verification_status;
//    @Column(columnDefinition = "tinyint default 0")
//    private DriverStatus status = DriverStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    public DriverStatus status;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLE", joinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "email")
    },
            inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID")
            }

    )
    private Set<Role> Role;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getDriver_name() {
//        return driver_name;
//    }
//
//    public void setDriver_name(String driver_name) {
//        this.driver_name = driver_name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getMobile_no() {
//        return mobile_no;
//    }
//
//    public void setMobile_no(String mobile_no) {
//        this.mobile_no = mobile_no;
//    }
//
//    public String getLicense_no() {
//        return license_no;
//    }
//
//    public void setLicense_no(String license_no) {
//        this.license_no = license_no;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getAadhar_no() {
//        return aadhar_no;
//    }
//
//    public void setAadhar_no(String aadhar_no) {
//        this.aadhar_no = aadhar_no;
//    }
//
//    public String getDriver_img() {
//        return driver_img;
//    }
//
//    public void setDriver_img(String driver_img) {
//        this.driver_img = driver_img;
//    }
//
//    public String getId_proof_img() {
//        return id_proof_img;
//    }
//
//    public void setId_proof_img(String id_proof_img) {
//        this.id_proof_img = id_proof_img;
//    }
//
//    public String getLicense_img() {
//        return license_img;
//    }
//
//    public void setLicense_img(String license_img) {
//        this.license_img = license_img;
//    }
//
//    public Boolean getVerification_status() {
//        return verification_status;
//    }
//
//    public void setVerification_status(Boolean verification_status) {
//        this.verification_status = verification_status;
//    }
//
//    public Set<com.JustAlo.Entity.Role> getRole() {
//        return Role;
//    }
//
//    public void setRole(Set<com.JustAlo.Entity.Role> role) {
//        Role = role;
//    }
}
