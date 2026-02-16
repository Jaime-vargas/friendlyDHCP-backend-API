package com.app.dhcp.service;

import com.app.dhcp.Valid.Valid;
import com.app.dhcp.dto.NetworkDto;
import com.app.dhcp.enums.ErrorMessages;
import com.app.dhcp.enums.HttpStatusError;
import com.app.dhcp.exeptionsHandler.HandleException;
import com.app.dhcp.mapper.Mapper;
import com.app.dhcp.model.Network;
import com.app.dhcp.repository.NetworkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetworkService {

    private final NetworkRepository networkRepository;

    public NetworkService(NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
    }

    // --- CRUD ---
    public List<NetworkDto> GetNetworkList(){
        List<Network> networkList = networkRepository.findAll();
        return networkList.stream().map(Mapper::entityToDto).toList();
    }

    public NetworkDto getNetworkById(Long networkId){
        Network network = networkRepository.findById(networkId).orElseThrow(
                () ->  new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ErrorMessages.CONFIG_NOT_FOUND.getMessage() + networkId)
        );
        return Mapper.entityToDto(network);
    }

    public NetworkDto createNetwork(NetworkDto networkDto){
        Network network = Mapper.dtoToEntity(networkDto);
        Valid.validNetworkData(network);
        return Mapper.entityToDto(networkRepository.save(network));
    }

    public NetworkDto updateNetwork(Long networkId, NetworkDto networkDto){
        Network network = networkRepository.findById(networkId).orElseThrow(
                () ->  new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ErrorMessages.CONFIG_NOT_FOUND.toString() + networkId)
        );
        network.setName(networkDto.getName());
        network.setSubnet(networkDto.getSubnet());
        network.setNetmask(networkDto.getNetmask());
        network.setStart_range(networkDto.getStart_range());
        network.setEnd_range(networkDto.getEnd_range());
        network.setDefault_lease_time(networkDto.getDefault_lease_time());
        network.setMax_lease_time(networkDto.getMax_lease_time());
        network.setRouter(networkDto.getRouter());
        network.setPrimary_dns(networkDto.getPrimary_dns());
        network.setSecondary_dns(networkDto.getSecondary_dns());
        Valid.validNetworkData(network);
        networkRepository.save(network);
        return Mapper.entityToDto(network);
    }

    public void deleteNetworkById(Long networkId){
        Network network = networkRepository.findById(networkId).orElseThrow(
                () ->  new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ErrorMessages.CONFIG_NOT_FOUND.toString() + networkId)
        );
        networkRepository.delete(network);
    }




}
