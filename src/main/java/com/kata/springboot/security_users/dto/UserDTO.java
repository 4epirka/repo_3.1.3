package com.kata.springboot.security_users.dto;

import java.util.Set;

public record UserDTO(
    Long id,
    String username,
    String name,
    int age,
    String country,
    Set<String> roles
){}
