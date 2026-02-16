package com.app.dhcp.Valid;

import com.app.dhcp.enums.ErrorMessages;
import com.app.dhcp.enums.FormatExceptionEnum;
import com.app.dhcp.enums.HttpStatusError;
import com.app.dhcp.exeption.FormatException;
import com.app.dhcp.exeptionsHandler.HandleException;
import com.app.dhcp.model.Device;
import com.app.dhcp.model.Network;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Valid {
    //Regex to validate data
    private static final Pattern patternName = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern patternMac = Pattern.compile("^([0-9A-F]{2}:){5}([0-9A-F]{2})$");
    private static final Pattern patternIp = Pattern.compile("^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}$");
    private static final Pattern patternTime = Pattern.compile("^[0-9]{1,4}$");

    public static void validDeviceData(Device device) {
        // Format Mac Address
        String macAddress = device.getMac_address();
        macAddress = macAddress.toUpperCase();
        macAddress = macAddress.replaceAll("[\\s-]","");

        // Format Name
        String name = device.getName();
        name = name.replaceAll("[\\s-.#]","");
        device.setName(name);

        Matcher matcherName = patternName.matcher(name);
        Matcher matcherMac = patternMac.matcher(macAddress);
        Matcher matcherIP = patternIp.matcher(device.getIp_address());
        if(!matcherMac.find()){
            throw new FormatException(FormatExceptionEnum.FORMAT_EXCEPTION.getMessage());
        }
        if(!matcherIP.find()){
            throw new FormatException(FormatExceptionEnum.FORMAT_EXCEPTION.getMessage());
        }
        if (!matcherName.find()){
            throw new FormatException(FormatExceptionEnum.FORMAT_EXCEPTION.getMessage());
        }

        device.setMac_address(macAddress);
    }

    public static void validNetworkData(Network network) {
        // Temp list of networks
        List<String> networkIpList = List.of(
                network.getSubnet(),
                network.getNetmask(),
                network.getStart_range(),
                network.getEnd_range(),
                network.getRouter(),
                network.getPrimary_dns(),
                network.getSecondary_dns()
        );

        networkIpList.forEach(networkIp -> {
            Matcher matcherIp = patternIp.matcher(networkIp);
            if(!matcherIp.find()){
                throw new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ErrorMessages.IP_ADDR_NOT_VALID.getMessage() + networkIp);
            }
        });

        Matcher matcherDefaultTime = patternTime.matcher(network.getDefault_lease_time());
        Matcher matcherMaxTime = patternTime.matcher(network.getMax_lease_time());
        if(!matcherDefaultTime.find() || !matcherMaxTime.find()){
            throw new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ErrorMessages.LEASE_TIME_NOT_VALID.getMessage());
        }
    }
}
