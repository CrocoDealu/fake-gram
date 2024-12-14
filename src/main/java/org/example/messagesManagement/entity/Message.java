package org.example.messagesManagement.entity;

import org.example.utils.Pair;
import org.example.sharedInfrastructure.Entity;

import java.time.LocalDateTime;

public class Message extends Entity<Pair<String, Pair<String, LocalDateTime>>> {
    private String message;
    private final LocalDateTime dateSend;
    private Message reply;

    public Message(String fromUsername, String toUsername, String message, LocalDateTime dateSend) {
        this.message = message;
        this.dateSend = dateSend;
        Pair<String, Pair<String, LocalDateTime>> entityId = new Pair<>(fromUsername, new Pair<>(toUsername, dateSend));
        setId(entityId);
        this.reply = null;
    }

    public String getFromUsername() {
        return getId().getFirst();
    }

    public String getToUsername() {
        return getId().getSecond().getFirst();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateSend() {
        return dateSend;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }
}
