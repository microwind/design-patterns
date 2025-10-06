package com.github.microwind.springwind.example.dao;

import com.github.microwind.springwind.example.model.User;

public class UserDao {
  public User findById(Long id) {
    System.out.println("find user by id: " + id);
    User user = new User();
    user.setId(id);
    user.setName("name:" + id);
    user.setAge(18);
    user.setEmail("email:" + id);
    return user;
  }

  public void save(User user) {
    System.out.println("save user: " + user);
  }
}
