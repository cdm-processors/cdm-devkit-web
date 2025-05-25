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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class E2ETest {

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
