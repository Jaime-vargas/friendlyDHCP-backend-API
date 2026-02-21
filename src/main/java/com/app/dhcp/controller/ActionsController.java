package com.app.dhcp.controller;

import com.app.dhcp.model.Configuration;
import com.app.dhcp.service.ConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/configuration")
public class ActionsController {

    private final ConfigurationService configurationService;
    public ActionsController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyConfiguration(){
        configurationService.doAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<Configuration> getConfiguration(){
        Configuration configuration = configurationService.getConfiguration();
        return ResponseEntity.ok().body(configuration);
    }

    @PostMapping()
    public ResponseEntity<Configuration> updateConfiguration(@RequestBody Configuration configuration){
        Configuration updatedConfiguration = configurationService.updateConfiguration(configuration);
        return ResponseEntity.ok().body(updatedConfiguration);
    }
}
