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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DockerService {
    private final DockerClient dockerClient;
    private int portCounter = 8081;
    private final ConcurrentHashMap<Integer, String> portContainerMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int START_PORT = 8081;
    private static final int END_PORT = 8130;
    private static final int MEMORY_LIMIT_MB = 512;
    private static final int PIDS_LIMIT = 20;
    private static final int CONTAINER_LIFETIME = 30;


    @Autowired
    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
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
        Ulimit[] ulimits = {new Ulimit("nproc", PIDS_LIMIT, PIDS_LIMIT + 10)};
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
        String containerUrl = "http://localhost:" + hostPort;
        monitorContainerActivity(container.getId(), hostPort);
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

    public void monitorContainerActivity(String containerId, int hostPort) {
        scheduler.schedule(() -> {
            if (!isContainerActive(hostPort)) {
                stopAndRemoveContainer(containerId, hostPort);
            }
        }, CONTAINER_LIFETIME, TimeUnit.MINUTES);
    }

    private boolean isContainerActive(int hostPort) {
        String containerUrl = "http://localhost:" + hostPort;
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForEntity(containerUrl, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void stopAndRemoveContainer(String containerId, int hostPort) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            portContainerMap.remove(hostPort);
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
