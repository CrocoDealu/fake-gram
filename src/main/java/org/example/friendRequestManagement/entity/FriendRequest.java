package org.example.friendRequestManagement.entity;

import org.example.utils.Pair;
import org.example.sharedInfrastructure.Entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequest extends Entity<Pair<String, String>> {
    private String status;
    private final LocalDateTime createdAt;

    public FriendRequest(String fromUsername, String toUsername, String status, LocalDateTime createdAt) {
        Pair<String, String> id = new Pair<>(fromUsername, toUsername);
        setId(id);
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getFromUsername() {
        return getId().getFirst();
    }

    public String getToUsername() {
        return getId().getSecond();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(getFromUsername(), that.getFromUsername()) && Objects.equals(getToUsername(), that.getToUsername()) && Objects.equals(status, that.status) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFromUsername(), getToUsername(), status, createdAt);
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                ", fromUserId=" + getFromUsername() +
                ", toUserId=" + getToUsername() +
                ", createdAt=" + createdAt +
                ", status='" + status + '\'' +
                '}';
    }
}
