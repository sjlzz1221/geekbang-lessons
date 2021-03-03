package org.geektimes.projects.user.repository;

import org.geektimes.projects.user.domain.User;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author: cola
 */
public class JNDIUserRepository implements UserRepository {

    public static final String INSERT_USER_DML_SQL =
            "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
                    "(?,?,?,?)";

    private Connection getConnection() {
        Connection connection = null;
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/UserPlatformDB");
            connection = ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public boolean save(User user) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(INSERT_USER_DML_SQL);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhoneNumber());
            int flag = preparedStatement.executeUpdate();
            return flag > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return null;
    }
}
