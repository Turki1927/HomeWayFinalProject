package com.example.homeway.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.engine.internal.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(40) unique not null")
    private String username;

    @Column(columnDefinition = "varchar(255) not null")
    private String password;

    @Column(columnDefinition = "varchar(100) not null")
    private String name;

    @Column(columnDefinition = "varchar(120) unique not null")
    private String email;

    @Column(columnDefinition = "varchar(20) not null")
    private String phone;

    @Column(columnDefinition = "varchar(50) not null")
    private String country;

    @Column(columnDefinition = "varchar(50) not null")
    private String city;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime created_at;

    @Column(columnDefinition = "varchar(30) not null check(role='CUSTOMER' or role='ADMIN' or role='WORKER' or role='INSPECTION_COMPANY' or role='MOVING_COMPANY' or role='MAINTENANCE_COMPANY' or role='REDESIGN_COMPANY' ) ")
    private String role;

    @Column(columnDefinition = "Boolean default false")
    private Boolean isSubscribed;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private Company company;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private Worker worker;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
