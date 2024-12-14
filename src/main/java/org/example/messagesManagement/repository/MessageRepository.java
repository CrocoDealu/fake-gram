package org.example.messagesManagement.repository;

import org.example.utils.Pair;
import org.example.messagesManagement.entity.Message;
import org.example.sharedInfrastructure.PagingRepository;

import java.time.LocalDateTime;

public interface MessageRepository extends PagingRepository<Pair<String, Pair<String, LocalDateTime>>, Message> {
}
