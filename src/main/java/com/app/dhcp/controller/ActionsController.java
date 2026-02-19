package com.app.dhcp.controller;

import com.app.dhcp.service.ConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ActionsController {

    private final ConfigurationService configurationService;
    public ActionsController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping("/apply-config")
    public ResponseEntity<?> crateConfigFile(){
        configurationService.doAll();
        return ResponseEntity.ok().build();
    }




}
