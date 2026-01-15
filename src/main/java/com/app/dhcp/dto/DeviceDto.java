package com.app.dhcp.dto;

import com.app.dhcp.enums.ErrorMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DeviceDto{
    Long id;
    String name;
    String mac_address;
    String ip_address;
    Long network_id;
    String network_name;
}
