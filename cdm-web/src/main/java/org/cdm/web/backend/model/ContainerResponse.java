package org.cdm.web.backend.model;

public class ContainerResponse {
    private String id;
    private String url;

    public ContainerResponse(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}