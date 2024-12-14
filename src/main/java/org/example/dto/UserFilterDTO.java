package org.example.dto;

import org.example.sharedInfrastructure.BaseFilter;
import org.example.userManagement.entity.User;

public class UserFilterDTO implements BaseFilter<User> {
    private final String username;

    public UserFilterDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
