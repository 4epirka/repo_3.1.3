package com.kata.springboot.security_users.service;

import com.kata.springboot.security_users.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> findAll();
    Role findByName(String name);
    Optional<Role> findById(Long id); // 🔥 добавляем этот метод
    void save(Role role);
}