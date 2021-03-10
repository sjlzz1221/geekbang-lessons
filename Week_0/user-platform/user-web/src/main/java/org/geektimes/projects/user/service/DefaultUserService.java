package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.repository.UserRepository;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author: cola
 */
public class DefaultUserService implements UserService {

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Resource(name = "bean/Validator")
    private Validator validator;

    private UserRepository userRepository;

    public DefaultUserService() {

        userRepository = new DatabaseUserRepository(new DBConnectionManager());

        //userRepository = new JNDIUserRepository();
    }

    @Override
    public boolean register(User user) {

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        violations.forEach(c -> {
            System.out.println(c.getMessage());
            throw new IllegalArgumentException(c.getMessage());
        });

        entityManager.persist(user);

        return true;
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
        return entityManager.find(User.class, id);
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
