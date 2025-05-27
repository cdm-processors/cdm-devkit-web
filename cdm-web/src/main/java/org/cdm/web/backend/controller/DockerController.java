package org.cdm.web.backend.controller;

import org.cdm.web.backend.docker.DockerService;
import org.cdm.web.backend.model.ContainerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DockerController {

    @Autowired
    private DockerService dockerService;

    @GetMapping("/home")
    public ResponseEntity<String> showHomePage() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(username);
    }

    @PostMapping("/create-container")
    public ResponseEntity<?> createContainer(@RequestParam String username) {
        try {
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }
            ContainerResponse response = dockerService.createContainer(username);
            return ResponseEntity.ok(response.getUrl());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error creating container: " + e.getMessage());
        }
    }
}
