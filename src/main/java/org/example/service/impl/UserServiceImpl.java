package org.example.service.impl;

import org.example.modul.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    UserRepository repository = new UserRepository();

    @Override
    public User create(User user) {
        User newuser = new User();
        newuser.setId(user.getId());
        newuser.setChatId(user.getChatId());
        newuser.setFullName(user.getFullName());
        newuser.setRole(user.getRole());
        newuser.setState(user.getState());
        repository.create(newuser);
        return user;
    }

    @Override
    public void update(long id, User user) {

    }

    @Override
    public User get(long id) {
        return null;
    }

    @Override
    public void delete(long chatId) {

    }

    @Override
    public List<User> getAll() {
        return repository.selectAll();
    }
}
