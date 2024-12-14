package org.example.authentificationManagement.repository;

import org.example.authentificationManagement.entity.UserCredential;
import org.example.sharedInfrastructure.PagingRepository;

public interface UserCredentialsRepository extends PagingRepository<String, UserCredential> {
}
