package kz.andersen.java_intensive_13.models;

import kz.andersen.java_intensive_13.enums.UserRole;

import java.time.ZonedDateTime;

public class User {

    private Long id;
    private String fistName;
    private String lastName;
    private UserRole userRole;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public User() {
    }

    public User(String fistName) {
        this.fistName = fistName;
    }

    public User(long id, String fistName) {
        this.id = id;
        this.fistName = fistName;
        this.userRole = UserRole.USER;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFistName() {
        return fistName;
    }

    public void setFistName(String name) {
        this.fistName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "fistName='" + fistName + '\'' +
                '}';
    }
}
