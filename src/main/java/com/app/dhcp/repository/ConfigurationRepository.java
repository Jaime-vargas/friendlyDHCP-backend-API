package com.app.dhcp.repository;

import com.app.dhcp.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration,Long> {
}
