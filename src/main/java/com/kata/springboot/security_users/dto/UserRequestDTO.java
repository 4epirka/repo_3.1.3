package com.kata.springboot.security_users.dto;

import java.util.Set;

public record UserRequestDTO(
        String username,
        String password,
        String name,
        int age,
        String country,
        Set<String> roles
) {}
