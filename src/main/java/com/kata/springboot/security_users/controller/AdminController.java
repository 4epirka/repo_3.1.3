package com.kata.springboot.security_users.controller;


import com.kata.springboot.security_users.dto.UserDTO;
import com.kata.springboot.security_users.dto.UserRequestDTO;
import com.kata.springboot.security_users.entity.Role;
import com.kata.springboot.security_users.entity.User;
import com.kata.springboot.security_users.service.RoleService;
import com.kata.springboot.security_users.service.UserService;
import org.springframework.http.ResponseEntity;
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

    public AdminController(UserService userService,
                           RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    // ✅ Получить всех пользователей
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // ✅ Получить одного пользователя по ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Создать нового пользователя
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

    // ✅ Обновить пользователя
    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO dto
    ) {
        return userService.findById(id)
                .map(existing -> {
                    existing.setUsername(dto.username());
                    existing.setPassword(dto.password());
                    existing.setName(dto.name());
                    existing.setAge(dto.age());
                    existing.setCountry(dto.country());
                    existing.setRoles(getRolesFromNames(dto.roles()));
                    User updated = userService.update(existing);
                    return ResponseEntity.ok(toDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Удалить пользователя
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

    // MVC NO REST
//    @GetMapping
//    public String listUsers(Model model) {
//        model.addAttribute("users", userService.findAll());
//        model.addAttribute("roles", roleService.findAll());
//        return "admin/users";
//    }
//
//    @GetMapping("/new")
//    public String createUserForm(Model model) {
//        model.addAttribute("user", new User());
//        model.addAttribute("roles", roleService.findAll());
//        return "admin/new";
//    }
//
//    @PostMapping
//    public String saveUser(@ModelAttribute("user") User user,
//                           @RequestParam(value = "roles", required = false) List<Long> roleIds) {
//        Set<Role> roles = new HashSet<>();
//        if (roleIds != null) {
//            for (Long id : roleIds) {
//                roleService.findById(id).ifPresent(roles::add);
//            }
//        }
//        user.setRoles(roles);
//        userService.save(user);
//        return "redirect:/admin";
//    }
//
//    @PostMapping("/update/{id}")
//    public String updateUser(@PathVariable("id") Long id,
//                             @ModelAttribute("user") User user,
//                             @RequestParam(value = "roles", required = false) List<Long> roleIds) {
//        user.setId(id);
//        Set<Role> roles = new HashSet<>();
//        if (roleIds != null) {
//            for (Long rid : roleIds) {
//                roleService.findById(rid).ifPresent(roles::add);
//            }
//        }
//        user.setRoles(roles);
//        userService.update(user);
//        return "redirect:/admin";
//    }
//
//    @PostMapping("/delete/{id}")
//    public String deleteUser(@PathVariable("id") Long id) {
//        userService.deleteById(id);
//        return "redirect:/admin";
//    }
