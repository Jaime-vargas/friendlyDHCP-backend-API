package com.app.dhcp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class NetworkDto {
    Long id;
    String subnet;
    String netmask;
    String start_range;
    String end_range;
    String default_lease_time;
    String max_lease_time;
    String router;
    String primary_dns;
    String secondary_dns;
    int devices_count;

}
