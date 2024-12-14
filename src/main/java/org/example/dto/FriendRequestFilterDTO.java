package org.example.dto;

import org.example.friendRequestManagement.entity.FriendRequest;
import org.example.sharedInfrastructure.BaseFilter;

import java.util.Optional;

public class FriendRequestFilterDTO implements BaseFilter<FriendRequest> {
    private final String username;
    private final String status;

    public FriendRequestFilterDTO(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public Optional<String>  getStatus() {
        return Optional.ofNullable(status);
    }
}
