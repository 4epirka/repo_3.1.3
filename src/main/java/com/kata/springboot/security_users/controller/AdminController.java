package com.kata.springboot.security_users.controller;


import com.kata.springboot.security_users.dto.UserDTO;
import com.kata.springboot.security_users.dto.UserRequestDTO;
import com.kata.springboot.security_users.entity.Role;
import com.kata.springboot.security_users.entity.User;
import com.kata.springboot.security_users.service.RoleService;
import com.kata.springboot.security_users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService,
                           RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        user.setName(dto.name());
        user.setAge(dto.age());
        user.setCountry(dto.country());
        user.setRoles(getRolesFromNames(dto.roles()));

        User saved = userService.save(user);
        return ResponseEntity.ok(toDTO(saved));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO dto
    ) {
        return userService.findById(id)
                .map(existing -> {
                    existing.setUsername(dto.username());
                    existing.setPassword(passwordEncoder.encode(dto.password()));
                    existing.setName(dto.name());
                    existing.setAge(dto.age());
                    existing.setCountry(dto.country());
                    existing.setRoles(getRolesFromNames(dto.roles()));
                    User updated = userService.update(existing);
                    return ResponseEntity.ok(toDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- Вспомогательные методы ---

    private UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getAge(),
                user.getCountry(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    private Set<Role> getRolesFromNames(Set<String> roleNames) {
        return roleNames.stream()
                .map(roleService::findByName)
                .collect(Collectors.toSet());
    }

}