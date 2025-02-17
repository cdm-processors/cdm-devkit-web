package org.cdm.web.backend.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import org.cdm.web.backend.model.ContainerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DockerService {
    private final DockerClient dockerClient;
    private final ConcurrentHashMap<Integer, String> portContainerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> heartbeatTimestamps = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int START_PORT = 8081;
    private static final int END_PORT = 8130;
    private static final int MEMORY_LIMIT_MB = 512;
    private static final int PIDS_LIMIT = 20;
    private static final long HEARTBEAT_TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(5);


    @Autowired
    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        scheduler.scheduleAtFixedRate(this::checkHeartbeats, 1, 1, TimeUnit.MINUTES);
    }

    private void ensureVolumePathExists(String volumePath) {
        try {
            Path path = Paths.get(volumePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                    Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
                }
            }
        } catch (UnsupportedOperationException e) {
            throw new RuntimeException("Your environment does not support Posix permissions: " + volumePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Error with creating directory: " + volumePath, e);
        }
    }

    public ContainerResponse createContainer(String username) throws InterruptedException {
        CreateContainerResponse container;
        dockerClient.pullImageCmd("nikolay251/cdm-web-coder1:tag").start().awaitCompletion();
        int hostPort = allocatePort();
        if (hostPort == -1) {
            throw new RuntimeException("No available ports for creating the container.");
        }
        ExposedPort containerPort = ExposedPort.tcp(8080);
        if (!System.getProperty("os.name").toLowerCase().contains("win")){
            String volumePath = "/data/" + username;
            ensureVolumePathExists(volumePath);
            Volume volume = new Volume("/home/coder/user");
            Bind bind = new Bind(volumePath, volume);
            container = dockerClient.createContainerCmd("nikolay251/cdm-web-coder1:tag")
                    .withExposedPorts(containerPort)
                    .withPortBindings(new PortBinding(Ports.Binding.bindPort(hostPort), containerPort))
                    .withBinds(bind)
                    .withMemory((long) (MEMORY_LIMIT_MB * 1024 * 1024))
                    .exec();
        } else {
            container = dockerClient.createContainerCmd("nikolay251/cdm-web-coder1:tag")
                    .withExposedPorts(containerPort)
                    .withPortBindings(new PortBinding(Ports.Binding.bindPort(hostPort), containerPort))
                    .withMemory((long) (MEMORY_LIMIT_MB * 1024 * 1024))
                    .exec();
        }
        dockerClient.startContainerCmd(container.getId()).exec();
        portContainerMap.put(hostPort, container.getId());
        heartbeatTimestamps.put(container.getId(), System.currentTimeMillis());
        String containerUrl = "http://localhost:" + hostPort;
        return new ContainerResponse(container.getId(), containerUrl);
    }

    private int allocatePort() {
        for (int port = START_PORT; port <= END_PORT; port++) {
            if (!portContainerMap.containsKey(port)) {
                return port;
            }
        }
        return -1;
    }

    public void updateHeartbeat(String containerId) {
        heartbeatTimestamps.put(containerId, System.currentTimeMillis());
    }

    private void checkHeartbeats() {
        long now = System.currentTimeMillis();
        for (String containerId : heartbeatTimestamps.keySet()) {
            if (now - heartbeatTimestamps.get(containerId) > HEARTBEAT_TIMEOUT_MILLIS) {
                int port = getPortByContainerId(containerId);
                if (port != -1) {
                    System.out.println("No heartbeat from container " + containerId + ". Stopping container...");
                    stopAndRemoveContainer(containerId, port);
                }
                heartbeatTimestamps.remove(containerId);
            }
        }
    }

    private int getPortByContainerId(String containerId) {
        for (Integer port : portContainerMap.keySet()) {
            if (portContainerMap.get(port).equals(containerId)) {
                return port;
            }
        }
        return -1;
    }

    private void stopAndRemoveContainer(String containerId, int hostPort) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            portContainerMap.remove(hostPort);
            heartbeatTimestamps.remove(containerId);
        } catch (Exception e) {
            System.err.println("Error while deleting container " + containerId + ": " + e.getMessage());
        }
    }

    @PreDestroy
    public void cleanupAllContainers() {
        for (int port : portContainerMap.keySet()) {
            stopAndRemoveContainer(portContainerMap.get(port), port);
        }
        scheduler.shutdown();
    }
}
