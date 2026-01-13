package com.app.dhcp.controller;
import com.app.dhcp.dto.NetworkDto;
import com.app.dhcp.service.NetworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/global-config")
public class NetworkController {

    private final NetworkService networkService;

    public NetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    @GetMapping
    public ResponseEntity<?> getNetworkList() {
        List<NetworkDto> networkDtoList = networkService.GetNetworkList();
        return ResponseEntity.ok().body(networkDtoList);
    }

    @GetMapping("/{networkId}")
    public ResponseEntity<?> getNetworkById(@PathVariable Long networkId){
        NetworkDto networkDto = networkService.getNetworkById(networkId);
        return ResponseEntity.ok().body(networkDto);
    }

    @PostMapping
    public ResponseEntity<?> createNetwork(@RequestBody NetworkDto networkDto){
        networkDto = networkService.createNetwork(networkDto);
        return ResponseEntity.ok().body(networkDto);
    }

    @PutMapping("/{networkId}")
    public ResponseEntity<?> updateNetwork(@PathVariable Long networkId, @RequestBody NetworkDto networkDto) {
        networkDto = networkService.updateNetwork(networkId, networkDto);
        return ResponseEntity.ok().body(networkDto);
    }

    @DeleteMapping("/{networkId}")
    public ResponseEntity<?> deleteNetworkById(@PathVariable Long networkId) {
        networkService.deleteNetworkById(networkId);
        return ResponseEntity.ok().build();
    }

}
