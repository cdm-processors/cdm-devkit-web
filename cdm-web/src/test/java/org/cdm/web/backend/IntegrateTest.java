package org.cdm.web.backend;


import org.cdm.web.backend.docker.DockerService;
import org.cdm.web.backend.model.ContainerResponse;
import org.cdm.web.backend.role.Role;
import org.cdm.web.backend.role.RoleRepository;
import org.cdm.web.backend.user.User;
import org.cdm.web.backend.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IntegrateTest {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DockerService dockerService;
    /*
    @BeforeEach
    void setUp() {
        userService.deleteUser(userService.findByUsername("testname").getId());
    }

     */

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, "ROLE_USER");
        roleRepository.save(role);

        List<User> users = userService.allUsers();

        for (User user : users) {
            userService.deleteUser(userService.findByUsername(user.getUsername()).getId());
        }

        dockerService.cleanupAllContainers();
    }

    @Test
    void saveUserTest() {
        User user = new User("testname", "testpass");
        userService.saveUser(user);
        assertNotNull(userService.findByUsername("testname"));
        userService.deleteUser(userService.findByUsername("testname").getId());
    }

    @Test
    void deleteUserTest() {
        User user = new User("testname", "testpass");
        userService.saveUser(user);
        userService.deleteUser(userService.findByUsername("testname").getId());
        assertNull(userService.findByUsername("testname"));
    }

    @Test
    void existingUserTest() {
        User user = new User("testname", "testpass");
        userService.saveUser(user);
        User newUser = new User("testname", "testpass");
        assertFalse(userService.saveUser(newUser));
        userService.deleteUser(userService.findByUsername("testname").getId());
    }

    @Test
    void findNoUserTest() {
        assertNull(userService.findByUsername("testname"));
    }

    @Test
    void getAllUsersTest() {
        List<User> usersList = new ArrayList<>();


        for (int i = 0; i < 50; i++) {
            usersList.add(new User(UUID.randomUUID().toString(),UUID.randomUUID().toString()));
        }


        //usersList.add(new User("testname", "testpass"));

        for (User user : usersList) {
            userService.saveUser(user);
        }

        List<User> refList = userService.allUsers();

        for (User user : refList) {
            assertTrue(usersList.contains(user));
        }

        for (User user : refList) {
            userService.deleteUser(userService.findByUsername(user.getUsername()).getId());
        }
    }

    @Test
    void saveUserAndCreateContainerTest() throws InterruptedException {
        User user = new User("testname", "testpass");
        userService.saveUser(user);
        ContainerResponse cr = dockerService.createContainer(user.getUsername());
        assertNotNull(cr);
    }

    @AfterEach
    void tearDown() {
        if (userService.findByUsername("testname") != null) {
            userService.deleteUser(userService.findByUsername("testname").getId());
        }
        dockerService.cleanupAllContainers();
    }
}
