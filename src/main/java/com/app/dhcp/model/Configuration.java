package com.app.dhcp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Configuration {
    @Id
    private Long id;
    String sshIpAddress;
    int sshPort;
    String sshUser;
    String sshPassword;
    String routeToCopyConfigFile;
    String commandToMoveConfigFile;
    String commandToRestartService;

    public Configuration(Long id){
        this.id = id;
    }
}
