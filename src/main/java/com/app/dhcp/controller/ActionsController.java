package com.app.dhcp.controller;

import com.app.dhcp.service.ActionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ActionsController {

    private final ActionsService actionsService;
    public ActionsController(ActionsService actionsService) {
        this.actionsService = actionsService;
    }

    @GetMapping("/apply-config")
    public ResponseEntity<?> crateConfigFile(){
        actionsService.doall();
        return ResponseEntity.ok().build();
    }
}
