package org.cdm.web.backend.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import jakarta.annotation.PreDestroy;
import org.cdm.web.backend.model.ContainerResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class DockerService {
    private final DockerClient dockerClient;
    private int portCounter = 8081;
    private final List<String> containerIds = new ArrayList<>();

    public DockerService() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375")
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    public ContainerResponse createContainer() {
        int hostPort = portCounter++;
        ExposedPort containerPort = ExposedPort.tcp(8080);
        CreateContainerResponse container = dockerClient.createContainerCmd("cdm-web-coder1")
                .withExposedPorts(ExposedPort.tcp(8080))
                .withPortBindings(new PortBinding(Ports.Binding.bindPort(hostPort), containerPort))
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
        containerIds.add(container.getId());
        String url = "http://localhost:" + hostPort;

        return new ContainerResponse(container.getId(), url);
    }

    @PreDestroy
    public void cleanupContainers() {
        for (String containerId : containerIds) {
            try {
                dockerClient.stopContainerCmd(containerId).exec();
                dockerClient.removeContainerCmd(containerId).exec();
            } catch (Exception e) {
                System.err.println("Ошибка при завершении контейнера: " + containerId);
                e.printStackTrace();
            }
        }
    }
}