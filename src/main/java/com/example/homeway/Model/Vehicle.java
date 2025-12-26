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
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20) not null")
    private String plateNumber;

    @Column(columnDefinition = "varchar(20) not null")
    private String type;

    @Column(columnDefinition = "double not null")
    private Double capacity;

    @Column(columnDefinition = "boolean not null")
    private Boolean available;

    @ManyToOne
    @JsonIgnore
    private Company company;

    @OneToMany(mappedBy = "vehicle")
    @JsonIgnore
    private Set<Request> requests;
}
