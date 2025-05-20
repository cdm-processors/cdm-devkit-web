package org.cdm.web.backend.controller;

import jakarta.validation.Valid;
import org.cdm.web.backend.email.OnRegistrationCompleteEvent;
import org.cdm.web.backend.email.VerificationToken;
import org.cdm.web.backend.user.User;
import org.cdm.web.backend.user.UserRepository;
import org.cdm.web.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.Locale;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }


    @PostMapping("/registration")
    public ResponseEntity<?> addUser(@ModelAttribute("userForm") @Valid User userForm,
                                   BindingResult bindingResult,
                                   Model model) {
        User userFromDB = userRepository.findByUsername(userForm.getUsername());

        if (userFromDB != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("User with email " + userForm.getUsername() + " already exists");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Validation errors");
        }

        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Passwords do not match");
        }

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(
            userForm,
            LocaleContextHolder.getLocale(),
            "http://localhost:8080/"
        ));

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("User registered successfully");
    }


    @GetMapping("/registrationConfirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token, @ModelAttribute("userForm") @Valid User userForm) {

        Locale locale = request.getLocale();

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html";
        }


        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = messages.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/badUser.html";
        }

        user.setEnabled(true);
        userService.saveUser(user);
        return "redirect:/login.html";
    }
}