package com.kata.springboot.security_users.service;

import com.kata.springboot.security_users.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserService {

    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);

    User save(User user);
    User update(User user);
    void deleteById(Long id);

}
