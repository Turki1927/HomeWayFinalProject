package com.example.homeway.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer {

    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "customer")
    private Set<Property> properties;

    @OneToMany(mappedBy = "customer")
    private Set<Request> requests;

    @OneToMany(mappedBy = "customer")
    private Set<Notification> notifications;

    @OneToMany(mappedBy = "customer")
    private Set<Review> reviews;
}
