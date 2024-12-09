package org.cdm.web.backend.controller;

import org.cdm.web.backend.docker.DockerService;
import org.cdm.web.backend.model.ContainerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DockerController {

    @Autowired
    private DockerService dockerService;

    @GetMapping("/home")
    public ModelAndView showHomePage() {
        return new ModelAndView("index");
    }

    @PostMapping("/create-container")
    public ModelAndView createContainer() throws InterruptedException {
        ContainerResponse response = dockerService.createContainer();
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("containerUrl", response.getUrl());
        return modelAndView;
    }
}
