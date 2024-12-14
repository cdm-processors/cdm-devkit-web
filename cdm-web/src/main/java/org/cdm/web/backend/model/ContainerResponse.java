package org.cdm.web.backend.model;

import lombok.Getter;

@Getter
public class ContainerResponse {
    private String id;
    private String url;

    public ContainerResponse(String id, String url) {
        this.id = id;
        this.url = url;
    }

}