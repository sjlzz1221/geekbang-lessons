package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.repository.JNDIUserRepository;
import org.geektimes.projects.user.repository.UserRepository;
import org.geektimes.projects.user.sql.DBConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author: cola
 */
public class DefaultUserService implements UserService {

    private UserRepository userRepository;

    public DefaultUserService() {

        userRepository = new DatabaseUserRepository(new DBConnectionManager());

        //userRepository = new JNDIUserRepository();
    }

    @Override
    public boolean register(User user) {

        return userRepository.save(user);
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
