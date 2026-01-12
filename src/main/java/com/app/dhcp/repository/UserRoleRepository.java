package com.app.dhcp.repository;

import com.app.dhcp.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    boolean existsByRole(String role);
    UserRole findByRole(String role);
}
