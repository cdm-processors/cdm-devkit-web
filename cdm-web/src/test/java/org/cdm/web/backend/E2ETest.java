package org.cdm.web.backend;


import org.cdm.web.backend.docker.DockerService;
import org.cdm.web.backend.email.VerificationToken;

import org.cdm.web.backend.email.VerificationTokenRepository;
import org.cdm.web.backend.role.Role;
import org.cdm.web.backend.role.RoleRepository;
import org.cdm.web.backend.user.User;
import org.cdm.web.backend.user.UserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Testcontainers
@ContextConfiguration(classes = CdmWebApplication.class)
@ImportResource({"application.properties"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class E2ETest {
    private static final Dotenv dotenv = Dotenv.configure()
        .directory("src/test/resources")
        .load();


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
        registry.add("spring.mail.username", () -> dotenv.get("MAIL_USER"));
        registry.add("spring.mail.password", () -> dotenv.get("MAIL_PASSWORD"));
    }


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DockerService dockerService;

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, "ROLE_USER");
        roleRepository.save(role);

        List<User> users = userService.allUsers();

        for (User user : users) {
            userService.deleteUser(userService.findByUsername(user.getUsername()).getId());
        }

        dockerService.cleanupAllContainers();

        List<VerificationToken> tokens = userService.getAllTokens();

        for (VerificationToken token : tokens) {
            userService.deleteToken(token);
        }
    }
    @Test
    void openLogin() {
        ResponseEntity<String> response = restTemplate.getForEntity("/login", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //System.out.println(response.getBody());
    }

    @Test
    void postTest() {
        String username = "testuser@example.com";
        String password = "12345";
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", "testuser@example.com");
        formData.add("password", password);
        formData.add("passwordConfirm", password);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/registration",
                formData,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        VerificationToken token = userService.getTokenByUsername("testuser@example.com");
        String urlTokenString = token.getToken();
        response = restTemplate.getForEntity("/registrationConfirm?token=" + urlTokenString, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertNotNull(userService.findByUsername(username));

        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("username", username);
        loginData.add("password", password);

        response = restTemplate.postForEntity(
                "/login",
                loginData,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    void badCredentialsTest() {
        String username = "testuser@example.com";
        String password = "12345";
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", "testuser@example.com");
        formData.add("password", password);
        formData.add("passwordConfirm", password);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/registration",
                formData,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        VerificationToken token = userService.getTokenByUsername("testuser@example.com");
        String urlTokenString = token.getToken();
        response = restTemplate.getForEntity("/registrationConfirm?token=" + urlTokenString, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertNotNull(userService.findByUsername(username));

        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("username", username);
        loginData.add("password", "password");

        response = restTemplate.postForEntity(
                "/login",
                loginData,
                String.class
        );
        System.out.println(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void registerAlreadyExistingUser() {
        String username = "testuser@example.com";
        String password = "12345";
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", "testuser@example.com");
        formData.add("password", password);
        formData.add("passwordConfirm", password);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/registration",
                formData,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        VerificationToken token = userService.getTokenByUsername("testuser@example.com");
        String urlTokenString = token.getToken();
        response = restTemplate.getForEntity("/registrationConfirm?token=" + urlTokenString, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertNotNull(userService.findByUsername(username));

        response = restTemplate.postForEntity(
                "/registration",
                formData,
                String.class
        );

        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.CREATED);
    }

    @AfterEach
    void tearDown() {
        List<User> users = userService.allUsers();

        for (User user : users) {
            userService.deleteUser(userService.findByUsername(user.getUsername()).getId());
        }

        dockerService.cleanupAllContainers();

        List<VerificationToken> tokens = userService.getAllTokens();

        for (VerificationToken token : tokens) {
            userService.deleteToken(token);
        }
    }
    
}
