package com.sunrisedental.model;

import java.io.Serializable;

/**
 * JavaBean / DTO: Role
 */
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ADMIN = "ADMIN";
    public static final String RECEPTIONIST = "RECEPTIONIST";
    public static final String DOCTOR = "DOCTOR";
    public static final String CASHIER = "CASHIER";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_RECEPTIONIST = "RECEPTIONIST";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_CASHIER = "CASHIER";

    private int roleId;
    private String roleName;
    private String description;

    public Role() {}

    public Role(int roleId, String roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
