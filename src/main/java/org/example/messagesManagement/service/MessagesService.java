package org.example.messagesManagement.service;

import org.example.utils.Pair;
import org.example.messagesManagement.entity.Message;
import org.example.messagesManagement.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MessagesService {
    private final MessageRepository messageRepository;

    public MessagesService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void addMessage(Message newMessage) {
        messageRepository.save(newMessage);
    }

    public void removeMessage(Message message) {
        messageRepository.delete(new Pair<>(message.getFromUsername(), new Pair<>(message.getToUsername(), LocalDateTime.now())));
    }

    public Iterable<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Pair<String, Pair<String, LocalDateTime>> messageId) {
        return messageRepository.findOne(messageId);
    }

    public Map<String, Vector<Message>> getMessagesGroupedByUser(String username) {
        return StreamSupport.stream(getAllMessages().spliterator(), false)
                .flatMap(message -> Stream.of(
                        Map.entry(message.getFromUsername(), message),
                        Map.entry(message.getToUsername(), message)
                ))
                .filter(entry -> {
                    String otherUsername = entry.getKey();
                    Message message = entry.getValue();
                    return otherUsername.equals(username) || message.getFromUsername().equals(username) || message.getToUsername().equals(username);
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.toCollection(Vector::new)
                        )
                ));
    }

    public void updateMessage(Message message) {
        messageRepository.update(message);
    }
}
