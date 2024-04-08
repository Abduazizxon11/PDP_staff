package org.example.service.impl;

import org.example.modul.BotState;
import org.example.modul.Role;
import org.example.modul.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository = new UserRepository();

    @Override
    public User create(User user) {
        System.out.println(user.toString());
        userRepository.create(user);
        return null;
    }

    @Override
    public void update(long chatId, User user) {
        user.setChatId(chatId);
        userRepository.update(user);
    }

    @Override
    public User get(long chatId) {
        return userRepository.selectById(chatId);
    }

    @Override
    public void delete(long chatId) {
        userRepository.delete(chatId);
    }

    @Override
    public List<User> getAll() {
        return userRepository.selectAll();
    }
}