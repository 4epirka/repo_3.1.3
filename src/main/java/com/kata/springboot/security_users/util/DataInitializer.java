package com.kata.springboot.security_users.util;


import com.kata.springboot.security_users.entity.Role;
import com.kata.springboot.security_users.entity.User;
import com.kata.springboot.security_users.repository.RoleRepository;
import com.kata.springboot.security_users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        if (roleRepository.findByName("ROLE_USER") == null) {
            roleRepository.save(roleUser);
        } else {
            roleUser = roleRepository.findByName("ROLE_USER");
        }

        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            roleRepository.save(roleAdmin);
        } else {
            roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setName("Admin");
            admin.setAge(30);
            admin.setCountry("USA");

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(roleUser);
            adminRoles.add(roleAdmin);
            admin.setRoles(adminRoles);

            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setName("Regular User");
            user.setAge(25);
            user.setCountry("Canada");

            Set<Role> userRoles = new HashSet<>();
            userRoles.add(roleUser);
            user.setRoles(userRoles);

            userRepository.save(user);
        }
        System.out.println("Data initialization complete: roles and test users created.");
    }
}