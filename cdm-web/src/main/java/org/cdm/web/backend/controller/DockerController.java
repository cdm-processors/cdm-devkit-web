package org.cdm.web.backend.controller;

import org.cdm.web.backend.docker.DockerService;
import org.cdm.web.backend.model.ContainerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DockerController {

    @Autowired
    private DockerService dockerService;

    @GetMapping("/home")
    public String showHomePage(Model model) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "index";
    }

    @PostMapping("/create-container")
    public ModelAndView createContainer(String username) throws InterruptedException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        ContainerResponse response = dockerService.createContainer(username);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("containerUrl", response.getUrl());
        modelAndView.addObject("containerId", response.getId());
        return modelAndView;
    }
}
