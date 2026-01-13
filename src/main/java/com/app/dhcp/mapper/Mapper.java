package com.app.dhcp.mapper;

import com.app.dhcp.dto.DeviceDto;
import com.app.dhcp.dto.NetworkDto;
import com.app.dhcp.model.Device;
import com.app.dhcp.model.Network;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Mapper {

    // --- DEVICE ---
    public static DeviceDto entityToDto(Device device){
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId(device.getId());
        deviceDto.setName(device.getName());
        deviceDto.setMac_address(device.getMac_address());
        deviceDto.setIp_address(device.getIp_address());
        deviceDto.setNetwork_id(device.getNetwork().getId());
        return deviceDto;
    }
    public static Device dtoToEntity(DeviceDto deviceDto){
        Device device = new Device();
        device.setName(deviceDto.getName());
        device.setMac_address(deviceDto.getMac_address());
        device.setIp_address(deviceDto.getIp_address());
        return device;
    }

    // --- GLOBAL CONFIG ---
    public static NetworkDto entityToDto(Network network){
        NetworkDto networkDto = new NetworkDto();
        networkDto.setId(network.getId());
        networkDto.setSubnet(network.getSubnet());
        networkDto.setNetmask(network.getNetmask());
        networkDto.setStart_range(network.getStart_range());
        networkDto.setEnd_range(network.getEnd_range());
        networkDto.setDefault_lease_time(network.getDefault_lease_time());
        networkDto.setMax_lease_time(network.getMax_lease_time());
        networkDto.setRouter(network.getRouter());
        networkDto.setPrimary_dns(network.getPrimary_dns());
        networkDto.setSecondary_dns(network.getSecondary_dns());
        networkDto.setDevices_count(
                Optional.ofNullable(network.getDevices())
                        .map(List::size)
                        .orElse(0)
        );
        return networkDto;
    }

    public static Network dtoToEntity(NetworkDto networkDto){
        Network network = new Network();
        network.setSubnet(networkDto.getSubnet());
        network.setNetmask(networkDto.getNetmask());
        network.setStart_range(networkDto.getStart_range());
        network.setEnd_range(networkDto.getEnd_range());
        network.setDefault_lease_time(networkDto.getDefault_lease_time());
        network.setMax_lease_time(networkDto.getMax_lease_time());
        network.setRouter(networkDto.getRouter());
        network.setPrimary_dns(networkDto.getPrimary_dns());
        network.setSecondary_dns(networkDto.getSecondary_dns());
        return network;
    }

}
