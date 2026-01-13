package com.app.dhcp.service;

import com.app.dhcp.dto.DeviceDto;
import com.app.dhcp.enums.ErrorMessages;
import com.app.dhcp.enums.HttpStatusError;
import com.app.dhcp.exeptionsHandler.HandleException;
import com.app.dhcp.mapper.Mapper;
import com.app.dhcp.model.Device;
import com.app.dhcp.model.Network;
import com.app.dhcp.repository.DeviceRepository;
import com.app.dhcp.repository.NetworkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final NetworkRepository networkRepository;

    public DeviceService(DeviceRepository deviceRepository, NetworkRepository networkRepository) {
        this.deviceRepository = deviceRepository;
        this.networkRepository = networkRepository;
    }

    // --- CRUD ---
    public List<DeviceDto> getDeviceList(){
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream().map(Mapper::entityToDto).toList();
    }

    public DeviceDto getDeviceById(Long deviceId){
        Device device = deviceRepository.findById(deviceId).orElseThrow(
                () ->  new HandleException(HttpStatus.BAD_REQUEST, HttpStatusError.BAD_REQUEST, ErrorMessages.DEVICE_NOT_FOUND.toString() +  deviceId)
        );
        return Mapper.entityToDto(device);
    }

    public DeviceDto createDevice(DeviceDto deviceDto){
        Device device = Mapper.dtoToEntity(deviceDto);
        Network network = networkRepository.findById(deviceDto.getNetwork_id()).orElseThrow(
                () ->  new HandleException(HttpStatus.BAD_REQUEST, HttpStatusError.BAD_REQUEST, ErrorMessages.CONFIG_NOT_FOUND.toString() + deviceDto.getNetwork_id())
        );
        device.setNetwork(network);
        device = deviceRepository.save(device);
        return Mapper.entityToDto(device);
    }

    public DeviceDto updateDevice(Long deviceId, DeviceDto deviceDto){
        deviceRepository.findById(deviceId).orElseThrow(
                () -> new HandleException(HttpStatus.BAD_REQUEST, HttpStatusError.BAD_REQUEST, ErrorMessages.DEVICE_NOT_FOUND.toString() + deviceId)
        );
        Device device = Mapper.dtoToEntity(deviceDto);
        device.setId(deviceId);
        device = deviceRepository.save(device);

        return Mapper.entityToDto(device);
    }

    public void deleteDevice(Long deviceId){
        Device device = deviceRepository.findById(deviceId).orElseThrow(
                () -> new HandleException(HttpStatus.BAD_REQUEST, HttpStatusError.BAD_REQUEST, ErrorMessages.DEVICE_NOT_FOUND.toString() + deviceId)
        );
        deviceRepository.delete(device);
    }
}
