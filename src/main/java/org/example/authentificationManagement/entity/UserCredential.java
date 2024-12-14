package org.example.authentificationManagement.entity;

import org.example.sharedInfrastructure.Entity;

import java.util.Objects;

public class UserCredential extends Entity<String> {
    private String password;

    public UserCredential(String username, String password) {
        setId(username);
        this.password = password;
    }

    public String getUsername() {
        return getId();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserCredential that = (UserCredential) o;
        return Objects.equals(getUsername(), that.getUsername()) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUsername(), password);
    }

    @Override
    public String toString() {
        return "UserCredential{" +
                "username='" + getUsername() + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
