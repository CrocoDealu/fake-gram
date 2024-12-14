package org.example.dto;

import org.example.sharedInfrastructure.Entity;

public class UserCredentialDTO extends Entity<Long> {
    private final String username;

    public UserCredentialDTO(Long id, String username) {
        super();
        this.id = id;
        this.username = username;
    }

    public UserCredentialDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
