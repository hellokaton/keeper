package com.example.keeper.service;

import com.example.keeper.model.User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    public User login(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    public User findByUsername(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("123456");
        return user;
    }

    public Set<String> findRoles(String username) {
        return new HashSet<>(Arrays.asList("admin"));
    }

    public Set<String> findPermissions(String username) {
        return new HashSet<>(Arrays.asList("user:edit", "user:view"));
    }
}
