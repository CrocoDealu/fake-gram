package org.example.dto;

import org.example.friendshipManagement.entity.Friendship;
import org.example.sharedInfrastructure.BaseFilter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

public class FriendshipFilterDTO implements BaseFilter<Friendship> {
    private final String username;

    private final Timestamp startDate;

    public FriendshipFilterDTO(String username, Timestamp startDate) {
        this.username = username;
        this.startDate = startDate;
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public Optional<Timestamp> getStartDate() {
        return Optional.ofNullable(startDate);
    }
}
