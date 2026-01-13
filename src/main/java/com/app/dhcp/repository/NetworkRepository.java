package com.app.dhcp.repository;

import com.app.dhcp.model.Network;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkRepository extends JpaRepository<Network, Long> {
}
