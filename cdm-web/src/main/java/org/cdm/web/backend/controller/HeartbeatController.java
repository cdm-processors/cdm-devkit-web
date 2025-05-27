package org.cdm.web.backend.controller;

import org.cdm.web.backend.docker.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HeartbeatController {

    @Autowired
    private DockerService dockerService;

    @PostMapping("/heartbeat")
    public void heartbeat(@RequestParam String containerId) {
        dockerService.updateHeartbeat(containerId);
    }
}
