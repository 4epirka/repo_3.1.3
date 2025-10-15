package com.kata.springboot.security_users.controller;


import com.kata.springboot.security_users.entity.Role;
import com.kata.springboot.security_users.entity.User;
import com.kata.springboot.security_users.service.RoleService;
import com.kata.springboot.security_users.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService,
                           RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleService.findAll());
        return "admin/users";
    }

    @GetMapping("/new")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "admin/new";
    }

    @PostMapping
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            for (Long id : roleIds) {
                roleService.findById(id).ifPresent(roles::add);
            }
        }
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("user") User user,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        user.setId(id);
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            for (Long rid : roleIds) {
                roleService.findById(rid).ifPresent(roles::add);
            }
        }
        user.setRoles(roles);
        userService.update(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}
