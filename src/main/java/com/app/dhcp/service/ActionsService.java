package com.app.dhcp.service;

import com.app.dhcp.dto.DeviceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionsService {

    private final DeviceService deviceService;
    private final NetworkService networkService;

    public ActionsService(DeviceService deviceService, NetworkService networkService) {
        this.deviceService = deviceService;
        this.networkService = networkService;
    }

    //Create dhpd.conf file
    public void createConfigFile(){
        String hosts;
        List<DeviceDto> deviceDtoList = deviceService.getDeviceList();

        deviceDtoList.forEach(device -> {
            String host = """
                host %s {
                    hardware ethernet %s;
                    fixed-address %s;
                }
                """.formatted(
                            device.getName(),
                            device.getMac_address(),
                            device.getIp_address()
                    );
            System.out.println(host);
        });
    }

}
