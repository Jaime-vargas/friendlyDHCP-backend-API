package com.app.dhcp.service;

import com.app.dhcp.dto.DeviceDto;
import com.app.dhcp.dto.NetworkDto;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class ActionsService {

    private final DeviceService deviceService;
    private final NetworkService networkService;
    public final Path filePath;
    private final String sshIpAddress;
    private final int sshPort;
    private final String sshUser;
    private final String sshPassword;
    private final String sshTaskCopyConfigFile;
    private final String sshTaskMoveConfigFile;
    private final String sshTaskRestartDhcpService;

    public ActionsService(DeviceService deviceService,
                          NetworkService networkService,
                          @Value("${config.file.path}") String filePath,
                          @Value("${ssh.ipaddress}") String sshIpAddress,
                          @Value("${ssh.port}") int sshPort,
                          @Value("${ssh.username}") String sshUser,
                          @Value("${ssh.password}") String sshPassword,
                          @Value("${ssh.task.copy-config-file}") String sshTaskCopyConfigFile,
                          @Value("${ssh.task.move-config-file}") String sshTaskMoveConfigFile,
                          @Value("${ssh.task.restart-dhcp-service}") String sshTaskRestartDhcpService)
    {
        this.deviceService = deviceService;
        this.networkService = networkService;
        this.filePath = Paths.get(filePath);
        this.sshIpAddress = sshIpAddress;
        this.sshPort = sshPort;
        this.sshUser = sshUser;
        this.sshPassword = sshPassword;
        this.sshTaskCopyConfigFile = sshTaskCopyConfigFile;
        this.sshTaskMoveConfigFile = sshTaskMoveConfigFile;
        this.sshTaskRestartDhcpService = sshTaskRestartDhcpService;
    }

    public void doall(){
        StringBuilder configString = generateConfigString();
        Path configFilePath = createConfigFile(configString);
        copyFileAndRestartDhcpService(configFilePath);
    }

    public void copyFileAndRestartDhcpService(Path configFilePath)  {
        //CONFIG ON SERVER FOR SUDO WITHOUT PASSWORD
        // create and valid visudo -f /etc/sudoers.d/userfile
        // user ALL=(root) NOPASSWD:/bin/mv /tmp/dhcpd.conf /etc/dhcp/dhcpd.conf,\
        // /bin/systemctl restart dhcpd

        try(SSHClient ssh = new SSHClient()){
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(sshIpAddress, sshPort);
            ssh.authPassword(sshUser, sshPassword);

            // METHOD TO CONNECT AS SFTP
            try(SFTPClient sftp = ssh.newSFTPClient()) {
                sftp.put(configFilePath.toString(), sshTaskCopyConfigFile);
            }

            // METHOD TO EXEC COMMANDS
            try(Session session = ssh.startSession()) {
                session.exec(sshTaskMoveConfigFile);
            }
            try(Session session = ssh.startSession()) {
                Session.Command command = session.exec(sshTaskRestartDhcpService);
                command.join();
                String stdout = new String(command.getInputStream().readAllBytes());
                String stderr = new String(command.getErrorStream().readAllBytes());
                System.out.println(stdout);
                System.out.println(stderr);
            }
        }catch (IOException e){
            System.out.println("NO se conecto");
            System.out.println(e.getMessage());
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

        System.out.println(configString.toString());

        return configString;
    }


}
