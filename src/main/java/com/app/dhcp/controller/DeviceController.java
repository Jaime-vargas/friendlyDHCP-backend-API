package com.app.dhcp.controller;

import com.app.dhcp.dto.DeviceDto;
import com.app.dhcp.model.Device;
import com.app.dhcp.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<?> getDevices(){
        List<DeviceDto> devices = deviceService.getDeviceList();
        return ResponseEntity.ok().body(devices);
    }

    @PostMapping
    public ResponseEntity<?> createDevice(@RequestBody DeviceDto deviceDto){
        deviceDto = deviceService.createDevice(deviceDto);
        return ResponseEntity.ok().body(deviceDto);
    }

    @PatchMapping("/{deviceId}/change-managed-status")
    public ResponseEntity<?> changeDeviceManagedStatus (@PathVariable Long deviceId){
        deviceService.changeDeviceManagedStatus(deviceId);
        return ResponseEntity.ok().build();
    }

    // DUE TO CHANGES ON ENTITY THIS METHOD IS USED TO ADAPT CURRENT DEVICES TO NEW ENTITY STRUCTURE, IT CAN BE DELETED IN THE FUTURE
    @PostMapping("/normalize")
    public ResponseEntity<?> normalize(){
        deviceService.validateAndNormalizeDevices();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{deviceId}")
    public ResponseEntity<?> updateDevice(@PathVariable Long deviceId, @RequestBody DeviceDto deviceDto){
        deviceDto = deviceService.updateDevice(deviceId, deviceDto);
        return ResponseEntity.ok().body(deviceDto);
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<?> deleteDevice(@PathVariable Long deviceId){
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.ok().build();
    }
}
