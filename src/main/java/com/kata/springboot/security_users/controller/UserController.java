package com.kata.springboot.security_users.controller;

import com.kata.springboot.security_users.entity.User;
import com.kata.springboot.security_users.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Страница личного кабинета
    @GetMapping("/user")
    public String showUserPage(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        return "user"; // thymeleaf шаблон user.html
    }
}