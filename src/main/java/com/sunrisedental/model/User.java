package com.sunrisedental.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JavaBean / DTO: User
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String username;
    private String passwordHash;
    private String salt;
    private String fullName;
    private String email;
    private String phone;
    private int roleId;
    private String roleName; // Populated from join
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {}

    public User(int userId, String username, String fullName, String email, String phone, int roleId, String roleName, boolean active) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
        this.roleName = roleName;
        this.active = active;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Role helper methods
    public boolean isAdmin() {
        return Role.ADMIN.equalsIgnoreCase(this.roleName);
    }

    public boolean isReceptionist() {
        return Role.RECEPTIONIST.equalsIgnoreCase(this.roleName);
    }

    public boolean isDoctor() {
        return Role.DOCTOR.equalsIgnoreCase(this.roleName);
    }

    public boolean isCashier() {
        return Role.CASHIER.equalsIgnoreCase(this.roleName);
    }
}
