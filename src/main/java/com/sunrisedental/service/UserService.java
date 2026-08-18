package com.sunrisedental.service;

import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;
import com.sunrisedental.util.PasswordUtil;
import com.sunrisedental.util.ValidationUtil;

import java.util.List;

/**
 * Service: UserService
 * Handles authentication, user management, and security credentials.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            return null;
        }

        User user = userDAO.authenticate(username.trim());
        if (user != null) {
            boolean valid = PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());
            if (valid) {
                return user;
            }
        }
        return null;
    }

    public User authenticate(String username, String password) {
        return login(username, password);
    }

    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public boolean registerUser(User user, String rawPassword) {
        if (user == null || !ValidationUtil.isNotEmpty(user.getUsername()) || !ValidationUtil.isNotEmpty(rawPassword)) {
            return false;
        }

        // Check if username already exists
        if (userDAO.findByUsername(user.getUsername().trim()) != null) {
            return false;
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(rawPassword, salt);
        user.setSalt(salt);
        user.setPasswordHash(hash);
        user.setActive(true);

        return userDAO.create(user);
    }

    public boolean createUser(String username, String rawPassword, String fullName, int roleId, String email, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setRoleId(roleId);
        user.setEmail(email);
        user.setPhone(phone);
        return registerUser(user, rawPassword);
    }

    public boolean updateUser(User user) {
        return userDAO.update(user);
    }

    public boolean resetPassword(int userId, String newRawPassword) {
        if (!ValidationUtil.isNotEmpty(newRawPassword)) return false;
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(newRawPassword, salt);
        return userDAO.updatePassword(userId, hash, salt);
    }

    public boolean toggleUserStatus(int userId, boolean active) {
        return userDAO.toggleStatus(userId, active);
    }

    public boolean toggleUserActive(int userId, boolean active) {
        return toggleUserStatus(userId, active);
    }
}
