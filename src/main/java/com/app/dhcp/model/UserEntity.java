package com.app.dhcp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

@Entity
public class UserEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;

    private String fullName;
    private String position;
    private String department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userRole_id", nullable = false)
    private UserRole userRole;

}
