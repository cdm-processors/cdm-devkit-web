package org.cdm.web.backend.controller;

import org.cdm.web.backend.docker.DockerService;
import org.cdm.web.backend.email.EmailService;
import org.cdm.web.backend.email.EmailServiceImpl;
import org.cdm.web.backend.email.OnRegistrationCompleteEvent;
import org.cdm.web.backend.model.ContainerResponse;
import org.cdm.web.backend.user.User;
import org.cdm.web.backend.role.Role;

import org.cdm.web.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService = new EmailServiceImpl();
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @GetMapping("/login")
    public ResponseEntity<User> getLoginForm() {
        // Возвращаем пустой объект User для заполнения на фронтенде
        User user = new User();
        user.setUsername(""); // Установите пустые значения, если хотите
        user.setPassword(""); // Установите пустые значения, если хотите
        return ResponseEntity.ok(user);
    }



    @PostMapping("/login")
    public ResponseEntity<?> loginUser (@RequestBody User user) {
        // Проверяем, что имя пользователя и пароль не null
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password must not be null");
        }

        // Проверяем, существует ли пользователь
        User existingUser  = userService.findByUsername(user.getUsername());
        if (existingUser  == null) {
            return ResponseEntity.status(404).body("User  not found");
        }

        // Логируем хешированный пароль и введенный пароль
        System.out.println("Stored password hash: " + existingUser .getPassword());
        System.out.println("Entered password: " + user.getPassword());

        // Проверяем, соответствует ли введенный пароль хешированному паролю
        boolean passwordMatch = passwordEncoder.matches(user.getPassword(), existingUser .getPassword());
        System.out.println("Password match: " + passwordMatch); // Логируем результат проверки пароля

        if (!passwordMatch) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // Если пароли совпадают, можно установить пользователя в контексте безопасности вручную
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(existingUser .getUsername(), null, existingUser .getAuthorities())
        );
        return ResponseEntity.ok("Login successful");
    }





    @GetMapping("/registration")
    public ResponseEntity<?> getRegistrationForm() {
        // Возвращаем пустой объект User для заполнения на фронтенде
        return ResponseEntity.ok(new User());
    }

    @PostMapping("/registration")
    public ResponseEntity<?> signupUser (@RequestBody User user) {
        // Check if the user already exists
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(409).body("User  already exists");
        }
        // Set the password and encode it
        user.setPassword(user.getPassword().trim());
        System.out.println("User  registered successfully: " + user.getUsername());
        System.out.println("Stored password hash: " + user.getPassword()); // Хешированный пароль
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER"))); // Assign default role

        // Save the user to the database
        userService.saveUser (user);
        emailService.sendSimpleMessage(user.getUsername(), "CdM Devkit Web", "You've been registered");
        return ResponseEntity.status(201).body("User  registered successfully");
    }

    @GetMapping("/home")
    public ResponseEntity<?> getHome() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return ResponseEntity.ok("Welcome to your home page, " + username + "!");
        } else {
            return ResponseEntity.status(401).body("Please log in to access this page.");
        }
    }

    @PostMapping("/create-container")
    public ResponseEntity<?> createContainer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Получаем имя пользователя из контекста

        System.out.println("Creating container for user: " + username);

        try {
            // Создаем контейнер и получаем URL
            ContainerResponse response = dockerService.createContainer(username);
            return ResponseEntity.ok(response.getUrl()); // Возвращаем URL контейнера
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при создании контейнера: " + e.getMessage());
        }


    }
}
