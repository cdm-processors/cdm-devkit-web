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
    private final List<String> containerIds = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<String, Boolean> containerActivity = new ConcurrentHashMap<>();

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
            throw new RuntimeException("Ваше окружение не поддерживает Posix права доступа: " + volumePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании директории: " + volumePath, e);
        }
    }

    public ContainerResponse createContainer(String username) throws InterruptedException {
        CreateContainerResponse container;
        dockerClient.pullImageCmd("nikolay251/cdm-web-coder1:tag").start().awaitCompletion();
        int hostPort = portCounter++;
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
                    .exec();
        } else {
            container = dockerClient.createContainerCmd("nikolay251/cdm-web-coder1:tag")
                    .withExposedPorts(containerPort)
                    .withPortBindings(new PortBinding(Ports.Binding.bindPort(hostPort), containerPort))
                    .exec();
        }
        dockerClient.startContainerCmd(container.getId()).exec();
        containerIds.add(container.getId());
        String containerUrl = "http://localhost:" + hostPort;
        monitorContainerActivity(container.getId(), containerUrl);
        return new ContainerResponse(container.getId(), containerUrl);
    }

    public void monitorContainerActivity(String containerId, String containerUrl) {
        Runnable monitorTask = () -> {
            if (!isContainerActive(containerUrl)) {
                stopAndRemoveContainer(containerId);
                containerActivity.remove(containerId);
            } else {
                containerActivity.put(containerId, true);
            }
        };
        scheduler.scheduleAtFixedRate(monitorTask, 1, 1, TimeUnit.MINUTES);
    }

    private boolean isContainerActive(String containerUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForEntity(containerUrl + "/api/activity", String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void stopAndRemoveContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            containerIds.remove(containerId);
        } catch (Exception e) {
            System.err.println("Ошибка при удалении контейнера " + containerId + ": " + e.getMessage());
        }
    }

    @PreDestroy
    public void cleanupAllContainers() {
        for (String containerId : new ArrayList<>(containerIds)) {
            stopAndRemoveContainer(containerId);
        }
        scheduler.shutdown();
    }
}
