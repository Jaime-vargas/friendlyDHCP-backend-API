package com.app.dhcp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Network {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String subnet;
    @Column(nullable=false)
    private String netmask;
    @Column(unique=true, nullable=false)
    private String start_range;
    @Column(unique=true, nullable=false)
    private String end_range;

    @Column(nullable=false)
    private String default_lease_time;
    @Column(nullable=false)
    private String max_lease_time;

    @Column(unique=true, nullable=false)
    private String router;
    @Column(nullable=false)
    private String primary_dns;
    @Column(nullable=false)
    private String secondary_dns;

    @OneToMany(mappedBy = "network", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Device> devices;
}
