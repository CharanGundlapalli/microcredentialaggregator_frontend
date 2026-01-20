package com.simats.microcredential;

public class User {
    private String userId;
    private String name;
    private String role;
    private String status;

    public User(String userId, String name, String role, String status) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}
