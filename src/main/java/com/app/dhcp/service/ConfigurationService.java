package com.app.dhcp.service;

import com.app.dhcp.dto.DeviceDto;
import com.app.dhcp.dto.NetworkDto;

import com.app.dhcp.exeptionsHandler.HandleException;
import com.app.dhcp.model.Configuration;
import com.app.dhcp.repository.ConfigurationRepository;
import jakarta.transaction.Transactional;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class ConfigurationService {

    private final DeviceService deviceService;
    private final NetworkService networkService;
    public final Path filePath;

    private final ConfigurationRepository configurationRepository;
    public ConfigurationService(DeviceService deviceService,
                                NetworkService networkService,
                                ConfigurationRepository configurationRepository,
                                @Value("${config.file.path}") String filePath )
    {
        this.deviceService = deviceService;
        this.networkService = networkService;
        this.configurationRepository = configurationRepository;
        this.filePath = Paths.get(filePath);
    }

    public Configuration getConfiguration(){
        if (configurationRepository.findById(1L).isEmpty()){
            configurationRepository.save(new Configuration(1L));
        }
        return configurationRepository.findById(1L).orElseThrow(
                () -> new HandleException(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, "Problem with configuration id: 1")
        );
    }

    @Transactional
    public Configuration updateConfiguration(Configuration configuration){
        Configuration configurationToEdit = configurationRepository.findById(1L).orElseThrow(
                () -> new HandleException(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, "Problem with configuration id: 1")
        );
        configurationToEdit.setSshIpAddress(configuration.getSshIpAddress());
        configurationToEdit.setSshPort(configuration.getSshPort());
        configurationToEdit.setSshUser(configuration.getSshUser());
        configurationToEdit.setSshPassword(configuration.getSshPassword());
        configurationToEdit.setRouteToCopyConfigFile(configuration.getRouteToCopyConfigFile());
        configurationToEdit.setCommandToMoveConfigFile(configuration.getCommandToMoveConfigFile());
        configurationToEdit.setCommandToRestartService(configuration.getCommandToRestartService());

        return configurationRepository.save(configurationToEdit);
    }

    public void doAll(){
        StringBuilder configString = generateConfigString();
        Path configFilePath = createConfigFile(configString);
        copyFileAndRestartDhcpService(configFilePath);
    }

    public void copyFileAndRestartDhcpService(Path configFilePath)  {

        Configuration configuration = configurationRepository.findById(1L).orElseThrow(
                () -> new HandleException(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, "Does not exist configuration")
        );

        try(SSHClient ssh = new SSHClient()){
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(configuration.getSshIpAddress(), configuration.getSshPort());
            ssh.authPassword(configuration.getSshUser(), configuration.getSshPassword());

            // METHOD TO CONNECT AS SFTP
            try(SFTPClient sftp = ssh.newSFTPClient()) {
                sftp.put(configFilePath.toString(), configuration.getRouteToCopyConfigFile());
            }

            // METHOD TO EXEC COMMANDS
            try(Session session = ssh.startSession()) {
                session.exec(configuration.getCommandToMoveConfigFile());
            }
            try(Session session = ssh.startSession()) {
                Session.Command command = session.exec(configuration.getCommandToRestartService());
            }
        }catch (IOException e){
            throw new HandleException(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, "Does not exist configuration: " + e.getMessage());
        }
    }

    public Path createConfigFile(StringBuilder configString){
        try{
            Files.createDirectories(filePath);
            Path configFilePath = filePath.resolve("dhcpd.conf");

            Files.write(configFilePath,
                    configString.toString().getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            return configFilePath;

        }catch(Exception e){
            System.out.println("Error creating config file");
        }

        return null;
    }


    //Create dhpd.conf file
    public StringBuilder generateConfigString(){
        StringBuilder configString = new StringBuilder();
        List<NetworkDto> networkDtoList = networkService.GetNetworkList();

        configString.append("authoritative;");
        configString.append("\n");
        configString.append("# NETWORKS");
        configString.append("\n\n");
        networkDtoList.forEach(network -> {
            configString.append("""
                    subnet %s netmask %s {
                        range %s %s;
                        option routers %s;
                        option domain-name-servers %s, %s;
                        default-lease-time %s;
                        max-lease-time %s;
                    }
                    """.formatted(
                            network.getSubnet(),
                    network.getNetmask(),
                    network.getStart_range(),
                    network.getEnd_range(),
                    network.getRouter(),
                    network.getPrimary_dns(),
                    network.getSecondary_dns(),
                    network.getDefault_lease_time(),
                    network.getMax_lease_time()
            ));
        });
        configString.append("\n");
        configString.append("# HOSTS");
        configString.append("\n\n");

        networkDtoList.forEach(network -> {
            List<DeviceDto> deviceDtoList = deviceService.getDeviceListByNetworkId(network.getId());
            deviceDtoList.forEach(deviceDto -> {
                configString.append("""
                host %s {
                    hardware ethernet %s;
                    fixed-address %s;
                }
                """.formatted(
                        deviceDto.getName(),
                        deviceDto.getMac_address(),
                        deviceDto.getIp_address()
                ));
            });
        });
        return configString;
    }


}
