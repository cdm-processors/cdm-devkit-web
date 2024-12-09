package org.cdm.web.backend.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import org.cdm.web.backend.model.ContainerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DockerService {
    private final DockerClient dockerClient;
    private int portCounter = 8081;
    private final List<String> containerIds = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public ContainerResponse createContainer() throws InterruptedException {
        dockerClient.pullImageCmd("nikolay251/cdm-web-coder1:tag").start().awaitCompletion();
        int hostPort = portCounter++;
        ExposedPort containerPort = ExposedPort.tcp(8080);
        CreateContainerResponse container = dockerClient.createContainerCmd("nikolay251/cdm-web-coder1:tag")
                .withExposedPorts(containerPort)
                .withPortBindings(new PortBinding(Ports.Binding.bindPort(hostPort), containerPort))
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
        containerIds.add(container.getId());

        String containerUrl = "http://localhost:" + hostPort;
        scheduleContainerTermination(container.getId(), containerUrl, 30);

        return new ContainerResponse(container.getId(), containerUrl);
    }

    private void scheduleContainerTermination(String containerId, String containerUrl, int timeoutMinutes) {
        scheduler.schedule(() -> {
            if (!isContainerActive(containerUrl)) {
                stopAndRemoveContainer(containerId);
            }
        }, timeoutMinutes, TimeUnit.MINUTES);
    }

    private boolean isContainerActive(String containerUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForEntity(containerUrl, String.class);
            return true;
        } catch (Exception e) {
            return false; // Контейнер недоступен
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
