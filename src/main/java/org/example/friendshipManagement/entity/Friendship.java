package org.example.friendshipManagement.entity;

import org.example.utils.Pair;
import org.example.sharedInfrastructure.Entity;

import java.time.LocalDateTime;

public class Friendship extends Entity<Pair<String, String>> {

    private final LocalDateTime createdAt;

    public Friendship(String username1, String username2, LocalDateTime createdAt) {
        Pair<String, String> id = new Pair<>(username1, username2);
        setId(id);
        this.createdAt = createdAt;
    }

    public String getUsername1() {
        return getId().getFirst();
    }

    public String getUsername2() {
        return getId().getSecond();
    }


    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                ", username1=" + getUsername1() +
                ", username2=" + getUsername2() +
                ", createdAt=" + createdAt +
                '}';
    }
}
