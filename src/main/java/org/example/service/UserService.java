package org.example.service;

import org.example.modul.User;

import java.util.List;

public interface UserService {
    User create(User user);
    void update(long chatId, User user);
    User get(long chatId);
    void delete(long chatId);
    List<User> getAll();
}
