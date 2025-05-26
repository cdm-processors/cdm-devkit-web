package org.cdm.web.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;

@Testcontainers
@SpringBootTest (classes = CdmWebApplication.class)
@ContextConfiguration(classes = CdmWebApplication.class)

class CdmWebApplicationTests {

    @Container
    private static final ComposeContainer composeContainer =
            new ComposeContainer(new File("src/test/resources/compose.yaml"))
                    .withExposedService("postgres", 5432)
                    .withLocalCompose(true);


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = String.format(
            "jdbc:postgresql://%s:%d/testdb",
            composeContainer.getServiceHost("postgres", 5432),
            composeContainer.getServicePort("postgres", 5432)
        );
        
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> "testuser");
        registry.add("spring.datasource.password", () -> "testpass");
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Test
    void contextLoads() {
    }

}
