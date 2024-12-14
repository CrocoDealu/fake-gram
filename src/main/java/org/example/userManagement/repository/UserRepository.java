package org.example.userManagement.repository;

import org.example.dto.UserFilterDTO;
import org.example.sharedInfrastructure.PagingRepository;
import org.example.userManagement.entity.User;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

public interface UserRepository extends PagingRepository<String, User> {
    public Page<User> findAllOnPage(Pageable pageable, UserFilterDTO userFilterDTO);
}
