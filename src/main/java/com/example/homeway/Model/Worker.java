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
public class Worker {

    @Id
    private Integer id;

    @Column(columnDefinition = "boolean")
    private Boolean isActive;

    @Column(columnDefinition = "boolean")
    private Boolean isAvailable;

    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    private Company company;

    @ManyToMany(mappedBy = "workers")
    @JsonIgnore
    private Set<Request> requests;

}
