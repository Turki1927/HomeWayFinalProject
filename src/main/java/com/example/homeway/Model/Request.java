package com.example.homeway.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(30) not null")
    private String status;

    @Column(columnDefinition = "varchar(30) not null")
    private String type;

    @Column(columnDefinition = "date")
    private LocalDate startDate;

    @Column(columnDefinition = "date")
    private LocalDate endDate;

    @Column(columnDefinition = "varchar(100) not null")
    private String timeWindow;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "varchar(255) not null")
    private String description;

    @Column(columnDefinition = "boolean not null")
    private Boolean isPaid;

    @ManyToOne
    @JsonIgnore
    private Customer customer;

    @ManyToOne
    @JsonIgnore
    private Company company;

    @ManyToOne
    @JsonIgnore
    private Property property;

    @ManyToOne
    @JsonIgnore
    private Vehicle vehicle;

    @ManyToMany
    @JoinTable(
            name = "request_workers",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "worker_id")
    )
    @JsonIgnore
    private Set<Worker> workers;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL)
    private Offer offer;

    @OneToMany(mappedBy = "request")
    private Report report;

    @OneToMany(mappedBy = "request")
    private Review review;

}
