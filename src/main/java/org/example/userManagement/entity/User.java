package org.example.userManagement.entity;

import org.example.sharedInfrastructure.Entity;

import java.util.Objects;

public class User extends Entity<String> {
    private String firstName;
    private String lastName;
    private String profileImage;
    private String bio;

    public User(String firstName, String lastName, String username, String profileImage, String bio) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.bio = bio;
        setId(username);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName);
    }

    @Override
    public String toString() {
        return "User = {" +
                "username=" + getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
