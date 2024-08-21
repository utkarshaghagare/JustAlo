package com.JustAlo.Entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Setter
@Getter

public class User implements Serializable {
    @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String username;
        private String name;
        private String email;
        private String password;
        private String contactNumber;
        private String otp;
        private LocalDateTime expirationTime;
    @Transient
    private boolean isVerified;

    @Setter
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLE", joinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "email")
    },
            inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID")
            }

    )
    private Set<Role> Role;

}
