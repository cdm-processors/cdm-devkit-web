package org.cdm.web.backend;


import org.cdm.web.backend.role.Role;
import org.cdm.web.backend.user.User;
import org.cdm.web.backend.user.UserRepository;
import org.cdm.web.backend.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest(UserService.class)
class UnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void userInitTest() {
        User user = new User();
        Role role = new Role(1L, "ROLE_USER");
        user.setUsername("username");
        user.setPassword("password");
        user.setPasswordConfirm("password");
        user.setRoles(Collections.singleton(role));
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("password", user.getPasswordConfirm());
        assertEquals(1, user.getRoles().size());
        assertEquals(user.getRoles().iterator().next(), role);
    }

    @Test
    void roleInitTest() {
        Role role = new Role(1L, "ROLE_USER");
        Set<User> userSet = new HashSet<>();
        StringBuilder name = new StringBuilder();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            User user = new User(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        }

        role.setUsers(userSet);

        for (User roleUser : role.getUsers()) {
            assertTrue(userSet.contains(roleUser));
        }
    }
}
