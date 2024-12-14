package org.example.authentificationManagement.service;

import org.example.authentificationManagement.entity.UserCredential;
import org.example.exceptions.ServiceValidationException;
import org.example.sharedInfrastructure.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

public class UserAuthentificationService {
    Repository<String, UserCredential> credentialsRepository;

    public UserAuthentificationService(Repository<String, UserCredential> credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    public Iterable<UserCredential> getUserCredentials() {
        return credentialsRepository.findAll();
    }

    public void addCredential(UserCredential userCredential) {
        credentialsRepository.save(userCredential);
    }


    public Optional<UserCredential> removeCredential(String username) {
        return credentialsRepository.delete(username);
    }

    public Optional<UserCredential> findCredential(String username) {
        return credentialsRepository.findOne(username);
    }
}
