package com.github.microwind.springwind.example.service;

import com.github.microwind.springwind.example.model.User;

public interface UserService {
    User getUserById(Long id);

    void saveUser(User user);
}