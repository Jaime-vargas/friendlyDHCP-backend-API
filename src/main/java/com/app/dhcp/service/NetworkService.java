package com.app.dhcp.service;

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
                () ->  new HandleException(HttpStatus.BAD_REQUEST, HttpStatusError.BAD_REQUEST, ErrorMessages.CONFIG_NOT_FOUND.toString() + networkId)
        );
        return Mapper.entityToDto(network);
    }

    public NetworkDto createNetwork(NetworkDto networkDto){
        Network network = Mapper.dtoToEntity(networkDto);
        return Mapper.entityToDto(networkRepository.save(network));
    }

    public NetworkDto updateNetwork(Long networkId, NetworkDto networkDto){
        networkRepository.findById(networkId).orElseThrow(
                () ->  new HandleException(HttpStatus.BAD_REQUEST, HttpStatusError.BAD_REQUEST, ErrorMessages.CONFIG_NOT_FOUND.toString() + networkId)
        );
        Network network = Mapper.dtoToEntity(networkDto);
        network.setId(networkId);
        networkRepository.save(network);
        return Mapper.entityToDto(network);
    }

    public void deleteNetworkById(Long networkId){
        Network network = networkRepository.findById(networkId).orElseThrow(
                () ->  new HandleException(HttpStatus.BAD_REQUEST, HttpStatusError.BAD_REQUEST, ErrorMessages.CONFIG_NOT_FOUND.toString() + networkId)
        );
        networkRepository.delete(network);
    }




}
